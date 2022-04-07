package mind.mqtt.cluster.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.entity.Message;
import mind.mqtt.cluster.config.NodeConfig;
import org.redisson.api.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


/**
 * 集群消息监听
 *
 * @author qiding
 */
@Component
@Slf4j
public abstract class AbstractMqttMessageListener implements ApplicationRunner {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void run(ApplicationArguments args) {
        this.newListener();
    }

    /**
     * 监听集群中，关于本机节点的消息
     */
    private void newListener() {
        RTopic topic = redissonClient.getTopic(NodeConfig.nodeName);
        topic.addListener(Message.class, (nodeId, message) -> {
            messageListener(nodeId.toString(), message);
        });
    }

    /**
     * 消息处理
     *
     * @param nodeId  节点id
     * @param message 消息
     */
    protected abstract void messageListener(String nodeId, Message message);

}
