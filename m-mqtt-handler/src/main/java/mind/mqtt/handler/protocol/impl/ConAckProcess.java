package mind.mqtt.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 * 确认连接报文
 *
 * @author qiding
 */
@Service
@Slf4j
public class ConAckProcess implements MqttProcess {

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        log.info("连接确认");
        log.info("获取发布过程存储");
        log.info("连接成功后，继续发送存储的消息");
    }

}
