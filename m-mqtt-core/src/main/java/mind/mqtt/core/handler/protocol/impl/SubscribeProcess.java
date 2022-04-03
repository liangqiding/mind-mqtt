package mind.mqtt.core.handler.protocol.impl;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import mind.common.utils.TopicUtil;
import mind.model.builder.MqttMessageBuilder;
import mind.model.entity.Subscribe;
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
        // 1. 校验并缓存订阅
        subscribeMsg.payload().topicSubscriptions().forEach(subscription -> {
            // topic 合法性校验
            if (TopicUtil.validTopicFilter(subscription.topicName())) {
                // 封装订阅实体
                Subscribe subscribe = new Subscribe()
                        .setTopicFilter(subscription.topicName())
                        .setClientId(clientId)
                        .setMqttQoS(subscription.qualityOfService().value());
                // 记录订阅成功的qos
                grantedQosList.add(subscribe.getMqttQoS());
                // 记录成功订阅，用于下一步发布保留消息
                subscribedTopicList.add(subscribe.getTopicFilter());
                // 缓存到会话
                mqttSessionStore.addSubscribe(subscribe);
            } else {
                // 记录订阅失败的qos
                grantedQosList.add(MqttQoS.FAILURE.value());
            }
        });
        // 2. 发布保留消息
        subscribedTopicList.forEach(s -> {

        });
        // 3. 应答客户端
        ctx.writeAndFlush(MqttMessageBuilder.newMqttSubAckMessage(subscribeMsg.variableHeader().messageId(), grantedQosList));
    }


}
