package mind.mqtt.client.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.client.handler.protocol.MqttProcess;
import mind.mqtt.client.retry.PubRelTask;
import org.springframework.stereotype.Service;

/**
 * 发布完成（qos2 发送端 第三步)
 * <p>
 * PUB-COMP报文是对PUB-REL报文的响应。它是QoS 2等级协议交换的第四个也是最后一个报文。
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PubCompProcess implements MqttProcess {
    private final PubRelTask pubRelTask;

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = variableHeader.messageId();
        // 停止pub-rel任务
        // 1. 清空前面的PUB-REL确认消息存储
        log.debug("subscriber -->> broker------收到PUB-COMP报文报文,qos2完成（qos2 发送端 第三步)");
        pubRelTask.stop(messageId);
    }

}
