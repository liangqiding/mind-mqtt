package mind.mqtt.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelMetadata;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import mind.mqtt.auth.IConnectAuth;
import mind.mqtt.handler.protocol.MqttProcess;
import mind.mqtt.store.ChannelManage;
import mind.mqtt.store.mqttStore.impl.MqttSessionStore;
import org.springframework.stereotype.Service;

/**
 * CONNECT连接报文
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
public class ConnectProcess implements MqttProcess {

    private final IConnectAuth connectAuth;

    private final MqttSessionStore mqttSessionStore;

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttConnectMessage mqttConnectMessage = (MqttConnectMessage) mqttMessage;
        // 鉴权
        if (!connectAuth.authenticate(ctx, mqttConnectMessage)) {
            ctx.writeAndFlush(this.connAckMsg(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
            return;
        }
        mqttSessionStore.test();
        // 广播上线消息，上线通知等作用。
        // 存储遗嘱消息
        // 返回ack报文
        ctx.writeAndFlush(this.connAckMsg(MqttConnectReturnCode.CONNECTION_ACCEPTED));
        // 在线状态更新
    }

    /**
     * 连接响应报文
     *
     * @param connectReturnCode 响应码
     */
    private MqttConnAckMessage connAckMsg(MqttConnectReturnCode connectReturnCode) {
        return (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(connectReturnCode, false), null);
    }

}
