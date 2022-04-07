package mind.mqtt.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.common.utils.MqttUtils;
import mind.model.builder.MqttMessageBuilder;
import mind.mqtt.client.handler.protocol.MqttProcess;
import mind.mqtt.client.server.MqttClient;
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
            log.error("不支持的协议版本");
            return;
        }
        log.debug("协议类型:{}", mqttMessage.fixedHeader().messageType().name());
        switch (mqttMessage.fixedHeader().messageType()) {
            case CONNACK:
                log.info("CONN-ACK，登录成功");
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
            case PINGRESP:
                mqttProcessMap.get("pingRespProcess").process(ctx, mqttMessage);
                break;
            case SUBACK:
                log.info("SUB-ACK，订阅成功");
                break;
            case UNSUBACK:
                log.info("UNSUB-ACK，取消订阅成功");
                break;
            default:
                log.error("不支持消息类型");
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("\n");
        log.debug("开始连接");
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
            log.info("发送心跳");
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
//            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
            MqttClient.socketChannel.writeAndFlush(MqttMessageBuilder.getPingReq());
//            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
