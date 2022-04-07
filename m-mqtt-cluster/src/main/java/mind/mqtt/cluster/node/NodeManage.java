package mind.mqtt.cluster.node;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.config.BrokerProperties;
import mind.mqtt.store.config.BorkerKey;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 集群节点管理
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NodeManage {

    private final RedissonClient redissonClient;

    public void add(String nodeName, JSONObject nodeInfo) {
        RMapCache<String, JSONObject> node = redissonClient.getMapCache(BorkerKey.CLUSTER_KEY);
        node.put(nodeName, nodeInfo);
    }

    public void remove(String nodeName) {
        redissonClient.getMapCache(BorkerKey.CLUSTER_KEY).remove(nodeName);
    }

    /**
     * 判断节点是否存在
     */
    public boolean containsNode(String nodeName) {
        return redissonClient.getMapCache(BorkerKey.CLUSTER_KEY).containsKey(nodeName);
    }

    /**
     * 获取所有集群节点
     */
    public List<String> getAllNode() {
        return redissonClient.getMapCache(BorkerKey.CLUSTER_KEY).keySet().stream().map(Object::toString).collect(Collectors.toList());
    }
}
