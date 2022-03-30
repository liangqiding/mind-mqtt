package mind.mqtt.store.mqttStore.impl;


import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import mind.model.entity.MqttSession;
import mind.model.config.BrokerProperties;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * mqtt 会话信息
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
public class MqttSessionStore {

    private final RedissonClient redissonClient;

    @Resource
    private BrokerProperties brokerProperties;

    private final String pre = "mind-mqtt:";
    private final String sessionMap = "sessionMap:";
    private final String subscribedMap = "subscribedMap:";
    private final String clientSubSet = "clientSubSet:";

    /**
     * redis中的缓存前缀，用于程序结束，释放缓存
     */
    private final Supplier<String> PREFIX = () -> pre + brokerProperties.getId() + ":";

    /**
     * session的缓存key
     */
    private final Supplier<String> MAP_SESSION_KEY = () -> PREFIX.get() + sessionMap;

    /**
     * 已订阅的缓存key
     */
    private final Function<String, String> MAP_SUBSCRIBED_KEY = topicFilter -> PREFIX.get() + subscribedMap + topicFilter;

    /**
     * client已订阅的缓存，用于客户端离线，快速清除订阅
     */
    private final Function<String, String> SET_CLIENT_SUBSCRIBED_KEY = clientId -> PREFIX.get() + clientSubSet + clientId;


    /**
     * 保存会话
     */
    public void saveSession(MqttSession mqttSession) {
        redissonClient.getMap(MAP_SESSION_KEY.get())
                .put(mqttSession.getClientId(), mqttSession);
    }

    /**
     * 获取会话信息
     */
    public MqttSession getSession(String clientId) {
        Object obj = redissonClient.getMap(MAP_SESSION_KEY.get()).get(clientId);
        return Optional.ofNullable(obj)
                .map(o -> JSON.parseObject(JSON.toJSONString(o), MqttSession.class))
                .orElse(null);
    }

    /**
     * 获取集群下所有连接数量
     */
    public int getAllNodeSessionCount() {
        AtomicInteger count = new AtomicInteger(0);
        redissonClient.getKeys().getKeysByPattern("*:" + sessionMap)
                .forEach(key -> count.addAndGet(redissonClient.getMap(key).size()));
        return count.get();
    }

    /**
     * 销毁本broker的所有缓存
     */
    public void destroyCache() {
        redissonClient.getKeys().deleteByPattern(PREFIX.get() + "*");
    }

    /**
     * 删除会话和订阅及所有缓存
     */
    public void delSessionAll(String clientId) {
        redissonClient.getMap(MAP_SESSION_KEY.get()).remove(clientId);
        this.removeSubAll(clientId);
    }

    /**
     * 添加订阅
     *
     * @param clientId    客户端id
     * @param topicFilter 订阅的topic
     * @param mqttQoS     qos
     */
    public void addSubscribe(String topicFilter, String clientId, int mqttQoS) {
        // 缓存订阅
        redissonClient.getMapCache(MAP_SUBSCRIBED_KEY.apply(topicFilter)).put(clientId, mqttQoS);
        // 双向保存,用于用户离线快速清除订阅
        redissonClient.getSet(SET_CLIENT_SUBSCRIBED_KEY.apply(clientId)).add(topicFilter);
    }

    /**
     * 客户端主动取消订阅
     *
     * @param clientId    客户端id
     * @param topicFilter 订阅的topic
     */
    public void removeSub(String clientId, String topicFilter) {
        redissonClient.getMapCache(MAP_SUBSCRIBED_KEY.apply(topicFilter)).remove(clientId);
        redissonClient.getSet(SET_CLIENT_SUBSCRIBED_KEY.apply(clientId)).remove(topicFilter);
    }

    /**
     * 删除客户端所有订阅
     *
     * @param clientId 客户端id
     */
    public void removeSubAll(String clientId) {
        // 获取client的已订阅列表，并移除订阅
        redissonClient.getSet(SET_CLIENT_SUBSCRIBED_KEY.apply(clientId))
                .forEach(topicFilter -> this.removeSub(clientId, (String) topicFilter));
        // 清除已订阅列表
        redissonClient.getSet(SET_CLIENT_SUBSCRIBED_KEY.apply(clientId)).delete();
    }
}
