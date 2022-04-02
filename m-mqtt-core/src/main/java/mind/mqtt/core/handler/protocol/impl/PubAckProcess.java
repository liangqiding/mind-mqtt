package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.core.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 * 发布确认
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PubAckProcess implements MqttProcess {


    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        // 1. 释放消息缓存
        log.debug("broker -->> publisher------收到PUB-ACK报文报文,qos1完成");
    }

}
