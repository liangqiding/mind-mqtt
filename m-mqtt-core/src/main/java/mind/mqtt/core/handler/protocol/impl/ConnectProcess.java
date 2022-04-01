package mind.mqtt.core.handler.protocol.impl;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.common.constant.NettyConstant;
import mind.model.entity.Message;
import mind.model.entity.MqttSession;
import mind.mqtt.auth.IConnectAuth;
import mind.model.config.BrokerProperties;
import mind.mqtt.core.handler.protocol.MqttProcess;
import mind.mqtt.store.channel.ChannelStore;
import mind.mqtt.store.mqttStore.impl.MqttSessionStore;
import org.springframework.stereotype.Service;


/**
 * CONNECT连接报文
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectProcess implements MqttProcess {

    private final IConnectAuth connectAuth;

    private final MqttSessionStore mqttSessionStore;

    private final BrokerProperties brokerProperties;

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttConnectMessage connectMsg = (MqttConnectMessage) mqttMessage;
        // 1. 鉴权
        if (!connectAuth.authenticate(ctx, connectMsg)) {
            ctx.writeAndFlush(this.connAckMsg(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
            return;
        }
        String clientId = connectMsg.payload().clientIdentifier();
        int keepAliveTime = connectMsg.variableHeader().keepAliveTimeSeconds();
        // 2. clientId做非空处理
        if (StrUtil.isBlank(clientId)) {
            ctx.writeAndFlush(this.connAckMsg(MqttConnectReturnCode.CONNECTION_REFUSED_CLIENT_IDENTIFIER_NOT_VALID));
            return;
        }
        // 3. 绑定频道
        ChannelStore.bind(ctx, clientId);
        // 4. 设置新的心跳时间
        this.setIdleTime(ctx, keepAliveTime);
        // 判断 uniqueId 是否在多个地方使用，如果在其他地方有使用，先解绑
        // 广播上线消息，上线通知等作用。
        // 5. 保存会话及遗嘱
        this.saveSession(ctx, connectMsg);
        // 6. 返回ack报文
        ctx.writeAndFlush(this.connAckMsg(MqttConnectReturnCode.CONNECTION_ACCEPTED));
        // 7. isCleanSession为false时, 客户端上线接收离线消息
        if (!connectMsg.variableHeader().isCleanSession()) {
            this.sendUndoneMessage(ctx);
        }
    }

    /**
     * 设置新的心跳时间
     */
    public void setIdleTime(ChannelHandlerContext ctx, int keepAliveTime) {
        // 若心跳时间大于0，则以客户端确定的时间为主
        if (keepAliveTime > 0) {
            if (ctx.pipeline().names().contains(NettyConstant.IDLE)) {
                ctx.pipeline().remove(NettyConstant.IDLE);
            }
            ctx.pipeline().addFirst(NettyConstant.IDLE, new IdleStateHandler(0, 0, Math.round(keepAliveTime * 1.5f)));
        }
    }

    /**
     * 响应报文
     *
     * @param connectReturnCode 响应码
     */
    private MqttConnAckMessage connAckMsg(MqttConnectReturnCode connectReturnCode) {
        return (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(connectReturnCode, false), null);
    }

    /**
     * 存储会话及遗嘱
     *
     * @param ctx        频道
     * @param connectMsg 连接报文
     */
    public void saveSession(ChannelHandlerContext ctx, MqttConnectMessage connectMsg) {
        boolean willFlag = connectMsg.variableHeader().isWillFlag();
        MqttSession mqttSession = new MqttSession();
        Message willMessage = null;
        // 存储遗嘱消息
        if (willFlag) {
            willMessage = new Message()
                    .setPacketId(1)
                    .setDup(false)
                    .setMessageType(MqttMessageType.PUBLISH)
                    .setTopic(connectMsg.payload().willTopic())
                    .setQos(connectMsg.variableHeader().willQos())
                    .setRetain(connectMsg.variableHeader().isWillRetain())
                    .setMessageBytes(connectMsg.payload().willMessageInBytes());
        }
        // 会话封装
        mqttSession
                .setBrokerId(brokerProperties.getId())
                .setChannelId(ctx.channel().id().asLongText())
                .setExpire(connectMsg.variableHeader().keepAliveTimeSeconds())
                .setClientId(connectMsg.payload().clientIdentifier())
                .setUsername(connectMsg.payload().userName())
                .setWillFlag(willFlag)
                .setCleanSession(false)
                .setWillMessage(willMessage);
        mqttSessionStore.saveSession(mqttSession);
    }

    /**
     * 如果cleanSession为0,即isCleanSession为false时, 客户端CleanSession=0时，上线接收离线消息，源码分析,需要重发同一clientId存储的未完成的QoS1和QoS2的DUP消息
     */
    public void sendUndoneMessage(final ChannelHandlerContext ctx) {
        // sendDup
        // sendDupRel
    }

}
