package mind.mqtt.core.handler.protocol.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.model.entity.Message;
import mind.model.entity.Subscribe;
import mind.mqtt.cluster.provider.MqttMessageProvider;
import mind.mqtt.core.dispatcher.MqttMessageDispatcher;
import mind.mqtt.core.handler.protocol.MqttProcess;
import mind.mqtt.store.ChannelManage;
import mind.mqtt.store.channel.ChannelStore;
import mind.mqtt.store.mqttStore.impl.MqttSessionStore;
import mind.mqtt.store.mqttStore.impl.Qos2MessageStoreImpl;
import mind.mqtt.store.mqttStore.impl.RetainMessageStoreImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

/**
 * 发布消息报文
 *
 * @author qiding
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PublishProcess implements MqttProcess {

    private final RetainMessageStoreImpl retainMessageStore;

    private final Qos2MessageStoreImpl qos2MessageStore;

    private final MqttMessageDispatcher mqttMessageDispatcher;

    private final MqttMessageProvider mqttMessageProvider;

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        log.debug("publisher -->> broker------发布消息，请求转发");
        MqttPublishMessage pubMsg = (MqttPublishMessage) mqttMessage;
        MqttQoS mqttQoS = pubMsg.fixedHeader().qosLevel();
        boolean retain = pubMsg.fixedHeader().isRetain();
        boolean dup = pubMsg.fixedHeader().isDup();
        String pubTopic = pubMsg.variableHeader().topicName();
        String clientId = ChannelStore.getClientId(ctx);
        int packetId = pubMsg.variableHeader().packetId();
        byte[] messageBytes = ByteBufUtil.getBytes(pubMsg.payload());
        // 1. 权限判断，在 MQTT v5.0 协议上已经支持应答一个相应的错误报文。
        Message message = new Message()
                .setMessageId(packetId)
                .setRetain(retain)
                .setMessageType(MqttMessageType.PUBLISH.value())
                .setDup(dup)
                .setMessageBytes(messageBytes)
                .setTimestamp(System.currentTimeMillis())
                .setQos(mqttQoS.value())
                .setFromClientId(clientId)
                .setTopic(pubTopic);
        // 消息保存
        this.retainMessage(message);
        // 应答publisher
        switch (mqttQoS) {
            case AT_MOST_ONCE:
                // 转发到其它subscriber
                mqttMessageDispatcher.publish(message);
                // 集群-消息转发到其它服务器节点
                mqttMessageProvider.relayPublish(message);
                break;
            case AT_LEAST_ONCE:
                Optional.of(packetId)
                        // qos1 消息id不能为-1
                        .filter(pId -> pId != -1)
                        .ifPresent(pId -> {
                            log.debug("broker -->> publisher------回复客户端，发布确认（QoS 1，消息确认）");
                            // 回复publisher PubAck
                            this.sendPubAckMessage(ctx, pId);
                            // 转发到其它subscriber
                            mqttMessageDispatcher.publish(message);
                            // 集群-消息转发到其它服务器节点
                            mqttMessageProvider.relayPublish(message);
                        });

                break;
            case EXACTLY_ONCE:
                Optional.of(packetId)
                        // qos2 消息id不能为-1
                        .filter(pId -> pId != -1)
                        // 是否重复的qos2消息
                        .filter(pid -> !this.qos2MessageStore.isRepeat(clientId, pid))
                        .ifPresent(pId -> {
                            // 缓存消息标识符
                            this.qos2MessageStore.put(clientId, message.getMessageId());
                            // 发送Rec，qos2接收端第一步，告诉客户端发布已接收，等客户端回复REL，收到REL后再清除标识符
                            log.debug("broker -->> publisher------回复客户端，发布已接收（QoS 2，第一步）");
                            // 回复publisher PubRec
                            this.sendPubRecMessage(ctx, pId);
                            // 转发到其它subscriber
                            mqttMessageDispatcher.publish(message);
                            // 集群-消息转发到其它服务器节点
                            mqttMessageProvider.relayPublish(message);
                        });
                break;
            case FAILURE:
                break;
            default:
                log.error("不支持的协议版本,qos:{}", message.getQos());
        }
    }

    /**
     * 消息保存
     */
    private void retainMessage(Message message) {
        if (message.isRetain()) {
            if (message.getMessageBytes().length == 0 || message.getQos() == AT_MOST_ONCE.value()) {
                retainMessageStore.remove(message.getTopic());
            } else {
                retainMessageStore.put(message);
            }
        }
    }


    private void sendPubAckMessage(ChannelHandlerContext ctx, int messageId) {
        ctx.writeAndFlush(MqttMessageBuilder.newMqttPubAckMessage(messageId));
    }

    private void sendPubRecMessage(ChannelHandlerContext ctx, int messageId) {
        ctx.writeAndFlush(MqttMessageBuilder.newMqttPubRecMessage(messageId));
    }
}
