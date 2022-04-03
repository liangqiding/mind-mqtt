package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.mqtt.core.handler.protocol.MqttProcess;
import mind.mqtt.core.retry.PublishTask;
import mind.mqtt.store.channel.ChannelStore;
import mind.mqtt.store.mqttStore.impl.Qos2MessageStoreImpl;
import org.springframework.stereotype.Service;

/**
 * 发布已接受（qos2 第一步）
 * <p>
 * PUB-REC报文是对QoS等级2的PUBLISH报文的响应。它是QoS 2等级协议交换的第二个报文。
 *
 * @author qiding
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PubRecProcess implements MqttProcess {

    private final PublishTask publishTask;

    private final Qos2MessageStoreImpl qos2MessageStore;

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        log.debug("subscriber -->> broker------发布已接受（qos2 第一步）");
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = variableHeader.messageId();
        // 1. qos2 丢弃存储的消息
        qos2MessageStore.remove(ChannelStore.getClientId(ctx), messageId);
        // 停止发布任务
        publishTask.stop(ChannelStore.getClientId(ctx), messageId);
        // 2. 回复PUB-REL并存储PUB-REL消息，等待客户端回复PUB-COM
        ctx.writeAndFlush(MqttMessageBuilder.newMqttPubRelMessage(variableHeader.messageId()));
        log.debug("broker -->> subscriber------回复客户端发布已释放 PUB-REL");
    }


}
