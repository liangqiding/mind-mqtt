package mind.mqtt.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 *  发布消息报文
 *
 * @author qiding
 */
@Service
@Slf4j
public class PubProcess implements MqttProcess {

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {

    }
}
