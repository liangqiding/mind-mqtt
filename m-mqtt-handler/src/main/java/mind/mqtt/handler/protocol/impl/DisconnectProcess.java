package mind.mqtt.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 * 断开连接报文
 *
 * @author qiding
 */
@Service
@Slf4j
public class DisconnectProcess implements MqttProcess {

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        ctx.channel().close();
        log.info("断开连接");
    }

}
