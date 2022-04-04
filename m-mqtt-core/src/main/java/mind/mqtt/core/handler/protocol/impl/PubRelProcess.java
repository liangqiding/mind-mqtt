package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.model.entity.Message;
import mind.mqtt.core.dispatcher.MqttMessageDispatcher;
import mind.mqtt.core.handler.protocol.MqttProcess;
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

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        log.debug("publisher -->> broker------发布已释放（qos2 接收端第二步）PUB-REL");
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = variableHeader.messageId();
        // 释放qos2 丢弃报文标识符
        qos2MessageStore.remove(ChannelStore.getClientId(ctx), messageId);
        log.debug("broker -->> publisher------回复客户端接收完成（qos2 第三步）PUB-COM");
        ctx.writeAndFlush(MqttMessageBuilder.newMqttPubCompMessage(messageId));
    }
}
