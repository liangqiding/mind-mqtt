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
        log.info("channelRead0:" + mqttMessage.payload().toString());
        // 判断 mqtt 消息解析是否正常
        if (!MqttUtils.isMqttMessage(ctx, mqttMessage)) {
            return;
        }
        switch (mqttMessage.fixedHeader().messageType()){
            case CONNECT:
                mqttProcessMap.get("connectProcess").process(ctx, mqttMessage);
                break;
            case DISCONNECT:
                break;
            default:
                break;
        }
        MqttMessageType connack = MqttMessageType.CONNACK;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final String clientId = (String) ctx.channel().attr(AttributeKey.valueOf("clientId")).get();
        log.warn("===频道无效channelInactive:" + clientId);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive:" + ctx.name());
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
