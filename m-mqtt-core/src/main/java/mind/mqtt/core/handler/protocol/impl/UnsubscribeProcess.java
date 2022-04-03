package mind.mqtt.core.handler.protocol.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import mind.model.builder.MqttMessageBuilder;
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
        ctx.writeAndFlush(MqttMessageBuilder.newMqttUnsubAckMessage(unsubscribeMessage.variableHeader().messageId()));
    }


}
