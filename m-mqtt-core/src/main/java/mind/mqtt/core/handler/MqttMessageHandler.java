package mind.mqtt.core.handler;

import cn.hutool.extra.emoji.EmojiUtil;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.common.utils.MqttUtils;
import mind.model.entity.Message;
import mind.model.entity.MqttSession;
import mind.mqtt.core.dispatcher.MqttMessageDispatcher;
import mind.mqtt.core.handler.protocol.MqttProcess;
import mind.mqtt.store.channel.ChannelStore;
import mind.mqtt.store.mqttStore.impl.MqttSessionStore;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;


/**
 * MQTT消息处理,单例启动
 *
 * @author qiding
 */
@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class MqttMessageHandler extends SimpleChannelInboundHandler<MqttMessage> {

    /**
     * 装配所有协议解析器
     */
    private final Map<String, MqttProcess> mqttProcessMap;

    private final MqttSessionStore mqttSessionStore;

    private final MqttMessageDispatcher mqttMessageDispatcher;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage mqttMessage) throws Exception {

        log.debug("\n");
        log.debug("channelId:" + ctx.channel().id());
        // 判断 mqtt 消息解析是否正常
        if (!MqttUtils.isMqttMessage(ctx, mqttMessage)) {
            log.error("不支持的协议版本");
            return;
        }
        log.debug("协议类型:{}", mqttMessage.fixedHeader().messageType().name());
        switch (mqttMessage.fixedHeader().messageType()) {
            case CONNECT:
                mqttProcessMap.get("connectProcess").process(ctx, mqttMessage);
                break;
            case CONNACK:
                mqttProcessMap.get("conAckProcess").process(ctx, mqttMessage);
                break;
            case PUBLISH:
                mqttProcessMap.get("publishProcess").process(ctx, mqttMessage);
                break;
            case PUBACK:
                mqttProcessMap.get("pubAckProcess").process(ctx, mqttMessage);
                break;
            case PUBREC:
                mqttProcessMap.get("pubRecProcess").process(ctx, mqttMessage);
                break;
            case PUBREL:
                mqttProcessMap.get("pubRelProcess").process(ctx, mqttMessage);
                break;
            case PUBCOMP:
                mqttProcessMap.get("pubCompProcess").process(ctx, mqttMessage);
                break;
            case SUBSCRIBE:
                mqttProcessMap.get("subscribeProcess").process(ctx, mqttMessage);
                break;
            case UNSUBSCRIBE:
                mqttProcessMap.get("unsubscribeProcess").process(ctx, mqttMessage);
                break;
            case PINGREQ:
                mqttProcessMap.get("pingRegProcess").process(ctx, mqttMessage);
                break;
            case DISCONNECT:
                mqttProcessMap.get("disconnectProcess").process(ctx, mqttMessage);
                break;
            default:
                log.error("不支持消息类型");
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String clientId = ChannelStore.getClientId(ctx);
        log.debug("\n");
        Optional.ofNullable(mqttSessionStore.getSession(clientId))
                .filter(MqttSession::isWillFlag)
                .ifPresent(mqttSession -> {
                    mqttMessageDispatcher.publish(mqttSession.getWillMessage());
                });
        log.warn("连接断开,clientId:{}", clientId);
        // 解除绑定
        ChannelStore.closeAndClean(clientId);
        // 删除会话及所有缓存包括订阅信息
        mqttSessionStore.delSessionAll(clientId);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("\n");
        log.info("成功建立连接,channelId：{}", ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("心跳事件时触发");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
