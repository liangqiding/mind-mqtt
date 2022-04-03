package mind.mqtt.core.handler.protocol.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.entity.Message;
import mind.mqtt.core.dispatcher.MqttMessageDispatcher;
import mind.mqtt.core.handler.protocol.MqttProcess;
import mind.mqtt.core.retry.PublishQos2Task;
import mind.mqtt.store.channel.ChannelStore;
import mind.mqtt.store.mqttStore.impl.Qos2MessageStoreImpl;
import org.springframework.stereotype.Service;

/**
 * 发布已释放（qos2 第二步）PUB-REL 报文是对 PUB-REC 报文的响应
 * <p>
 * 接收到该消息，服务器确认QOS2消息接收完成，开始转发广播消息，并回复 PUB-COM报文
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PubRelProcess implements MqttProcess {

    private final Qos2MessageStoreImpl qos2MessageStore;

    private final MqttMessageDispatcher mqttMessageDispatcher;


    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        log.debug("publisher -->> broker------发布已释放（qos2 第二步）PUB-REL");
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = variableHeader.messageId();
        Message qos2Message = qos2MessageStore.getQos2Message(ChannelStore.getClientId(ctx), messageId);
        // 已完成qos2消息接收，开始广播转发消息到其它客户端
        mqttMessageDispatcher.publish(qos2Message);
        log.debug("broker -->> publisher------回复客户端接收完成（qos2 第三步）PUB-COM");
        ctx.writeAndFlush(this.pubCompMessage(messageId));
    }

    public MqttMessage pubCompMessage(int messageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
    }
}
