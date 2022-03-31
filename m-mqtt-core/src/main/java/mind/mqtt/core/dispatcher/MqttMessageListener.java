package mind.mqtt.core.dispatcher;

import lombok.RequiredArgsConstructor;
import mind.model.entity.Message;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @author qiding
 */
@Component
@RequiredArgsConstructor
public class MqttMessageListener {

    private final RedissonClient redissonClient;

    public void addListener() {
        RTopic topic = redissonClient.getTopic("anyTopic");
        topic.addListener(Message.class, new MessageListener<Message>() {
            @Override
            public void onMessage(CharSequence charSequence, Message message) {

            }
        });
    }
}
