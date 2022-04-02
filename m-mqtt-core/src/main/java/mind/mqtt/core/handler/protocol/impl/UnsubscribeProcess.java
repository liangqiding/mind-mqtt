package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import mind.mqtt.core.handler.protocol.MqttProcess;
import mind.mqtt.store.channel.ChannelStore;
import mind.mqtt.store.mqttStore.impl.MqttSessionStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 取消订阅报文
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
public class UnsubscribeProcess implements MqttProcess {

    private final MqttSessionStore mqttSessionStore;

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttUnsubscribeMessage unsubscribeMessage = (MqttUnsubscribeMessage) mqttMessage;
        List<String> topicFilters = unsubscribeMessage.payload().topics();
        mqttSessionStore.removeSub(ChannelStore.getClientId(ctx), topicFilters);
        ctx.writeAndFlush(this.unsubAckMessage(unsubscribeMessage.variableHeader().messageId()));
    }

    public MqttUnsubAckMessage unsubAckMessage(int messageId) {
        return (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
    }

}
