package mind.mqtt.auth.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.auth.IConnectAuth;
import org.springframework.stereotype.Service;

/**
 * 客户端连接授权
 *
 * @author qiding
 */
@Service
@Slf4j
public class ConnectAuth implements IConnectAuth {

    @Override
    public boolean authenticate(ChannelHandlerContext ctx, MqttConnectMessage mqttConnectMessage) {
        return true;
    }
}
