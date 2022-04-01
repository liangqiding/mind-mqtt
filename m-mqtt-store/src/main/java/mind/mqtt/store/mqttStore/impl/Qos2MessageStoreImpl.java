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
public class Qos2MessageStoreImpl {

    private final RedissonClient redissonClient;

    /**
     * qos2消息存储
     */
    public void put(Message message) {
        RMapCache<Integer, Message> mapCache = redissonClient.getMapCache(RedisKey.QOS2_MSG_FUN.apply(message.getFromClientId()));
        if (message.getExpireTime() > 0) {
            mapCache.put(message.getPacketId(), message, message.getExpireTime(), TimeUnit.SECONDS);
        } else {
            mapCache.put(message.getPacketId(), message);
        }
    }

    /**
     * 获取存储的消息
     *
     * @param clientId 客户端
     * @param brokerId 消息id
     */
    public Message getQos2Message(String clientId, int brokerId) {
        RMapCache<Integer, Message> mapCache = redissonClient.getMapCache(RedisKey.QOS2_MSG_FUN.apply(clientId));
        return mapCache.get(brokerId);

    }

    /**
     * 删除该消息
     */
    public void remove(String clientId, int brokerId) {
        redissonClient.getMapCache(RedisKey.QOS2_MSG_FUN.apply(clientId)).remove(brokerId);
    }
}
