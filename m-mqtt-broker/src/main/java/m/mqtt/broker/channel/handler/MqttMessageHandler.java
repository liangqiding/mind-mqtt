package m.mqtt.broker.channel.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * MQTT消息处理
 *
 * @author qiding
 */
@ChannelHandler.Sharable
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttMessageHandler extends SimpleChannelInboundHandler<MqttMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        log.info("channelRead0:" + msg.payload().toString());
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
