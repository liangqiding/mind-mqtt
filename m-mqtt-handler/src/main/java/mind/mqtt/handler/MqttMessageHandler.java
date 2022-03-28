package mind.mqtt.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.common.utils.MqttUtils;
import mind.mqtt.handler.protocol.MqttProcess;
import mind.mqtt.handler.protocol.impl.ConnectProcess;
import org.springframework.stereotype.Component;

import java.util.Map;


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

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage mqttMessage) throws Exception {
        log.debug("\n");
        log.debug("channelId:" + ctx.channel().id());
        // 判断 mqtt 消息解析是否正常
        if (!MqttUtils.isMqttMessage(ctx, mqttMessage)) {
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
                mqttProcessMap.get("pubProcess").process(ctx, mqttMessage);
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
                log.error("未知的消息协议");
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final String clientId = (String) ctx.channel().attr(AttributeKey.valueOf("clientId")).get();
        log.warn("频道无效,channelId：{}", clientId);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("正在建立连接,channelId：{}", ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                Channel channel = ctx.channel();
                String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
                // 发送遗嘱消息
                log.info("发送遗嘱消息" + clientId);
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
