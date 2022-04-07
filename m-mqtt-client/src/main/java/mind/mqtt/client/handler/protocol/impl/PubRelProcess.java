package mind.mqtt.client.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.mqtt.client.handler.protocol.MqttProcess;
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

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        log.debug("publisher -->> client------发布已释放（qos2 接收端第二步）PUB-REL");
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = variableHeader.messageId();
        log.debug("client -->> publisher------发布已完成（qos2 接收端第三步）PUB-COMP");
        ctx.writeAndFlush(MqttMessageBuilder.newMqttPubCompMessage(messageId));
    }
}
