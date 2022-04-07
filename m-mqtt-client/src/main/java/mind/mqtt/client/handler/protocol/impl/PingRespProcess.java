package mind.mqtt.client.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.client.handler.protocol.MqttProcess;
import org.springframework.stereotype.Service;

/**
 * ping报文
 *
 * @author qiding
 */
@Service
@Slf4j
public class PingRespProcess implements MqttProcess {

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        log.debug("服务器响应ping");
    }
}
