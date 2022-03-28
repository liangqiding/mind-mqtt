package mind.mqtt.auth;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnectMessage;

/**
 * 连接认证
 *
 * @author qiding
 */
public interface IConnectAuth {

    /**
     * 连接认证
     *
     * @param ctx                ctx
     * @param mqttConnectMessage 连接报文
     * @return b
     */
    boolean authenticate(ChannelHandlerContext ctx, MqttConnectMessage mqttConnectMessage);

}
