package mind.mqtt.core.handler.protocol.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.entity.Message;
import mind.model.entity.Subscribe;
import mind.mqtt.core.handler.protocol.MqttProcess;
import mind.mqtt.store.ChannelManage;
import mind.mqtt.store.channel.ChannelStore;
import mind.mqtt.store.mqttStore.impl.MqttSessionStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发布消息报文
 *
 * @author qiding
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PubProcess implements MqttProcess {

    private final MqttSessionStore mqttSessionStore;

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttPublishMessage pubMsg = (MqttPublishMessage) mqttMessage;
        MqttQoS mqttQoS = pubMsg.fixedHeader().qosLevel();
        boolean retain = pubMsg.fixedHeader().isRetain();
        String pubTopic = pubMsg.variableHeader().topicName();
        String clientId = ChannelStore.getClientId(ctx);
        int packetId = pubMsg.variableHeader().packetId();
        List<Subscribe> subscribes = mqttSessionStore.searchTopic(pubTopic);
        // 1. 权限判断，在 MQTT v3.1 和 v3.1.1 协议中，发布操作被拒绝后服务器无任何报文错误返回，这是协议设计的一个缺陷。但在 MQTT v5.0 协议上已经支持应答一个相应的错误报文。
        // 转发逻辑
        switch (mqttQoS) {
            case AT_MOST_ONCE:
                subscribes.forEach(subscribe -> {
                    byte[] messageBytes = new byte[pubMsg.payload().readableBytes()];
                    pubMsg.payload().getBytes(pubMsg.payload().readerIndex(), messageBytes);
                    ByteBuf payload = pubMsg.payload();
                    ChannelManage.sendByCid(subscribe.getClientId(), this.publishMessage(pubTopic, 0, MqttQoS.AT_MOST_ONCE, ByteBufUtil.getBytes(payload), false, false));
                });
                break;
            case AT_LEAST_ONCE:
                subscribes.forEach(subscribe -> {
                    byte[] messageBytes = new byte[pubMsg.payload().readableBytes()];
                    pubMsg.payload().getBytes(pubMsg.payload().readerIndex(), messageBytes);
                    ByteBuf payload = pubMsg.payload();
                    ChannelManage.sendByCid(subscribe.getClientId(), this.publishMessage(pubTopic, 0, MqttQoS.AT_MOST_ONCE, ByteBufUtil.getBytes(payload), false, false));
                });
                this.sendPubAckMessage(ctx, packetId);
                break;
            case EXACTLY_ONCE:
                subscribes.forEach(subscribe -> {
                    byte[] messageBytes = new byte[pubMsg.payload().readableBytes()];
                    pubMsg.payload().getBytes(pubMsg.payload().readerIndex(), messageBytes);
                    ByteBuf payload = pubMsg.payload();
                    ChannelManage.sendByCid(subscribe.getClientId(), this.publishMessage(pubTopic, 0, MqttQoS.AT_MOST_ONCE, ByteBufUtil.getBytes(payload), false, false));
                });
                this.sendPubRecMessage(ctx, packetId);
                break;
            case FAILURE:
                break;
            default:
                log.error("不支持的协议版本,qos:{}", mqttQoS.value());
        }

    }

    public void send(List<Subscribe> subscribes, String pubTopic, int packetId, MqttQoS mqttQoS, byte[] messageBytes) {
        subscribes.forEach(subscribe -> {
            ChannelManage.sendByCid(subscribe.getClientId(), this.publishMessage(pubTopic, packetId, mqttQoS, messageBytes, false, false));
        });
    }

    public MqttPublishMessage publishMessage(String topic, int packetId, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
        return (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH, dup, mqttQoS, retain, 0),
                new MqttPublishVariableHeader(topic, packetId), Unpooled.buffer().writeBytes(messageBytes));
    }


    private void sendPubAckMessage(ChannelHandlerContext ctx, int messageId) {
        MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
        ctx.writeAndFlush(pubAckMessage);
    }

    private void sendPubRecMessage(ChannelHandlerContext ctx, int messageId) {
        MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
        ctx.writeAndFlush(pubRecMessage);
    }
}
