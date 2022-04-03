package mind.mqtt.core.dispatcher;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.model.entity.Message;
import mind.model.entity.Subscribe;
import mind.mqtt.core.retry.PublishQos2Task;
import mind.mqtt.store.ChannelManage;
import mind.mqtt.store.mqttStore.impl.MqttSessionStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息分发器,消息广播
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MqttMessageDispatcher {

    private final MqttSessionStore mqttSessionStore;

    private final PublishQos2Task publishQos2Task;

    /**
     * 消息转发
     *
     * @param message mqtt消息
     */
    public void publish(Message message) {
        List<Subscribe> subscribes = mqttSessionStore.searchTopic(message.getTopic());
        subscribes.forEach(subscribe -> {
            // 转发的qos取决于用户订阅
            MqttQoS mqttQoS = MqttQoS.valueOf(Math.min(message.getQos(), subscribe.getMqttQoS()));
            MqttPublishMessage publishMessage = MqttMessageBuilder.newPublishMessage(message
                    .setQos(mqttQoS.value())
                    .setToClientId(subscribe.getClientId())
            );
            switch (mqttQoS) {
                case AT_MOST_ONCE:
                case AT_LEAST_ONCE:
                    log.debug("broker -->> subscriber------开始转发qos0或qos1消息");
                    ChannelManage.sendByCid(subscribe.getClientId(), publishMessage);
                    break;
                case EXACTLY_ONCE:
                    log.debug("broker -->> subscriber------开始转发qos2消息消息");
                    publishQos2Task.start(message);
                    break;
                case FAILURE:
                    break;
                default:
                    log.error("不支持的协议版本,qos:{}", message.getQos());
            }
        });
    }


}