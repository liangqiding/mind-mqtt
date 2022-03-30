package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.core.handler.protocol.MqttProcess;
import mind.mqtt.store.channel.ChannelStore;
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
        log.info("{} 主动断开连接", ChannelStore.getClientId(ctx));
    }

}
