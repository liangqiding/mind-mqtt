package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.mqtt.core.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 * ping报文
 *
 * @author qiding
 */
@Service
@Slf4j
public class PingRegProcess implements MqttProcess {


    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        log.info("ping请求");
        ctx.writeAndFlush(MqttMessageBuilder.getPingRegMeg());
    }
}
