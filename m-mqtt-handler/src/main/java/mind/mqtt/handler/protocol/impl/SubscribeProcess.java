package mind.mqtt.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import mind.mqtt.auth.IConnectAuth;
import mind.mqtt.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 * 订阅报文
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
public class SubscribeProcess implements MqttProcess {


    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {

    }

}
