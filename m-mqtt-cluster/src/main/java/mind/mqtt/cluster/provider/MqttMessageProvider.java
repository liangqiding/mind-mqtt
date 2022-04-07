package mind.mqtt.cluster.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.config.BrokerProperties;
import mind.model.entity.Message;
import mind.mqtt.cluster.config.NodeConfig;
import mind.mqtt.cluster.node.NodeManage;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * 消息转发到集群的其它节点
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MqttMessageProvider {

    private final RedissonClient redissonClient;

    private final NodeManage nodeManage;


    /**
     * 转发到集群的其它节点
     */
    public void relayPublish(Message message) {
        for (String nodeId : nodeManage.getAllNode()) {
            // 发给非本机的其它节点
            if (nodeId.equals(NodeConfig.nodeName)) {
                continue;
            }
            redissonClient.getTopic(nodeId).publish(message);
        }
    }

}
