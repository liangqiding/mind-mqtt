package mind.mqtt.client.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.entity.Message;
import mind.mqtt.client.handler.protocol.MqttProcess;
import mind.mqtt.client.retry.PubRelTask;
import mind.mqtt.client.retry.PublishTask;
import org.springframework.stereotype.Service;

/**
 * 发布已接受（qos2 发送端 第二步）
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

    private final PubRelTask pubRelTask;


    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = variableHeader.messageId();
        log.debug("subscriber -->> broker------发布已接受（qos2 发送端 第二步）");
        publishTask.stop(messageId);
        pubRelTask.start(new Message().setMessageId(messageId));
    }


}
