package mind.mqtt.core.handler.protocol.impl;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import mind.mqtt.core.handler.protocol.MqttProcess;
import mind.mqtt.store.channel.ChannelStore;
import mind.mqtt.store.mqttStore.impl.MqttSessionStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅报文
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
public class SubscribeProcess implements MqttProcess {

    private final MqttSessionStore mqttSessionStore;

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttSubscribeMessage subscribeMsg = (MqttSubscribeMessage) mqttMessage;
        String clientId = ChannelStore.getClientId(ctx);
        List<Integer> grantedQosList = new ArrayList<>();
        List<String> subscribedTopicList = new ArrayList<>();
        // 校验并缓存订阅
        subscribeMsg.payload().topicSubscriptions().forEach(subscription -> {
            String topicFilter = subscription.topicName();
            MqttQoS mqttQoS = subscription.qualityOfService();
            if (this.validTopicFilter(topicFilter)) {
                grantedQosList.add(mqttQoS.value());
                subscribedTopicList.add(topicFilter);
                mqttSessionStore.addSubscribe(topicFilter, clientId, mqttQoS.value());
            } else {
                grantedQosList.add(MqttQoS.FAILURE.value());
            }
        });
        // 发布保留消息
        subscribedTopicList.forEach(s -> {

        });
        ctx.writeAndFlush(subAckMessage(subscribeMsg.variableHeader().messageId(), grantedQosList));
    }

    /**
     * 订阅响应消息
     */
    public MqttSubAckMessage subAckMessage(int messageId, List<Integer> mqttQosList) {
        return (MqttSubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                new MqttSubAckPayload(mqttQosList));
    }

    /**
     * topic校验
     */
    private boolean validTopicFilter(String topicFilter) {
        // 以#或+符号开头的、以/符号结尾的订阅按非法订阅处理, 这里没有参考标准协议
        if (StrUtil.startWith(topicFilter, '+') || StrUtil.endWith(topicFilter, '/')) {
            return false;
        }
        if (StrUtil.contains(topicFilter, '#')) {
            // 如果出现多个#符号的订阅按非法订阅处理
            if (StrUtil.count(topicFilter, '#') > 1) {
                return false;
            }
        }
        if (StrUtil.contains(topicFilter, '+')) {
            //如果+符号和/+字符串出现的次数不等的情况按非法订阅处理
            return StrUtil.count(topicFilter, '+') == StrUtil.count(topicFilter, "/+");
        }
        return true;
    }
}
