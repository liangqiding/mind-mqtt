package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.core.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 * 发布完成（qos2 第三步)
 * <p>
 * PUB-COMP报文是对PUB-REL报文的响应。它是QoS 2等级协议交换的第四个也是最后一个报文。
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PubCompProcess implements MqttProcess {


    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        // 1. 清空前面的确认消息存储 释放dupRel和msg
        log.debug("收到PUB-COMP报文报文,qos2完成");
    }

}
