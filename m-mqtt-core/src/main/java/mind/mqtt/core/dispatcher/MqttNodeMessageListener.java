package mind.mqtt.core.dispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.config.BrokerProperties;
import mind.model.entity.Message;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.redisson.api.listener.SetObjectListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 集群消息监听
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MqttNodeMessageListener {

    private final RedissonClient redissonClient;

    private final BrokerProperties brokerProperties;

    @PostConstruct
    public void init() {
        this.addListener();
    }

    /**
     * 监听集群中，关于本机节点的消息
     */
    public void addListener() {
        RTopic topic = redissonClient.getTopic(brokerProperties.getId());
        topic.addListener(Message.class, new MessageListener<Message>() {
            @Override
            public void onMessage(CharSequence charSequence, Message message) {
                go();
            }
        });
    }

    @Async("taskExecutor")
    public void go() {

    }
}
