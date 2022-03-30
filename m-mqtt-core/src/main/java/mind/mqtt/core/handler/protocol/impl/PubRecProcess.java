package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.core.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 *  发布已接受（qos2 第一步）
 *
 * @author qiding
 */
@Service
@Slf4j
public class PubRecProcess implements MqttProcess {

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {

    }
}
