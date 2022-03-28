package mind.mqtt.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.RequiredArgsConstructor;
import mind.mqtt.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 * 发布已释放（qos2 第二步）
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
public class PubRelProcess implements MqttProcess {


    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {

    }

}
