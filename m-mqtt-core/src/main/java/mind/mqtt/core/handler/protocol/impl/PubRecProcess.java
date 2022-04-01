package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.core.handler.protocol.MqttProcess;
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
public class PubRecProcess implements MqttProcess {

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        log.debug("发布已接受（qos2 第一步）");
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        // 1. qos2 获取发布的存储
        // 2. 回复并存储PUB-REL消息，等待客户端回复PUB-COM
        // 3.
        ctx.writeAndFlush(pubRelMessage(variableHeader.messageId()));
    }

    public MqttMessage pubRelMessage(int messageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
    }
}
