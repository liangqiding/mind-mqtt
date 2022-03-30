package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 响应PING请求消息
     */
    public static final MqttMessage PING_REG_MEG = MqttMessageFactory.newMessage(
            new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        log.info("ping请求");
        ctx.writeAndFlush(PING_REG_MEG);
    }
}
