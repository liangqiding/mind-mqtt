package mind.mqtt.store.mqttStore.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.common.utils.TopicUtil;
import mind.model.entity.Message;
import mind.mqtt.store.config.RedisKey;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 存储消息
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RetainMessageStoreImpl {

    private final RedissonClient redissonClient;

    /**
     * 消息存储
     */
    public void put(Message message) {
        RMapCache<String, Message> mapCache = redissonClient.getMapCache(RedisKey.RETAIN_MSG_KEY);
        if (message.getExpireTime() > 0) {
            mapCache.put(message.getTopic(), message, message.getExpireTime(), TimeUnit.SECONDS);
        } else {
            mapCache.put(message.getTopic(), message);
        }
    }

    /**
     * 获取存储的消息
     *
     * @param topicFilter 客户端订阅的topic
     */
    public List<Message> searchRetainMessage(String topicFilter) {
        RMapCache<String, Message> mapCache = redissonClient.getMapCache(RedisKey.RETAIN_MSG_KEY);
        List<Message> retainMessageList = new ArrayList<>();
        mapCache.entrySet().stream()
                .filter(entry -> TopicUtil.match(topicFilter, entry.getKey()))
                .forEach(entry -> retainMessageList.add(entry.getValue()));
        return retainMessageList;
    }

    /**
     * 删除该消息
     */
    public void remove(String topic) {
        redissonClient.getMapCache(RedisKey.RETAIN_MSG_KEY).remove(topic);
    }
}
