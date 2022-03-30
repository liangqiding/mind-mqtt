package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.RequiredArgsConstructor;
import mind.mqtt.core.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 * 发布确认
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
public class PubAckProcess implements MqttProcess {


    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {

    }

}
