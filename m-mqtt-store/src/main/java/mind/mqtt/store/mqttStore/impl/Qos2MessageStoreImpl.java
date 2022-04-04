package mind.mqtt.store.mqttStore.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.store.config.BorkerKey;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * qos2标识符存储，参考mqtt5协议文档
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Qos2MessageStoreImpl {

    private final RedissonClient redissonClient;

    /**
     * 参考mqtt5协议文档
     * qos2消息标识符存储，用于判断消息是否重复,在没收到pub-com报文前，相同id的消息将被过滤
     */
    public void put(String clientId, int messageId) {
        RMapCache<Integer, Integer> mapCache = redissonClient.getMapCache(BorkerKey.QOS2_MSG_FUN.apply(clientId));
        mapCache.put(messageId, 1, 60 * 30, TimeUnit.SECONDS);

    }

    /**
     * 判断消息是否重复确保qos2消息只接收一次
     *
     * @param clientId  客户端
     * @param messageId 消息id
     */
    public boolean isRepeat(String clientId, int messageId) {
        RMapCache<Integer, Integer> mapCache = redissonClient.getMapCache(BorkerKey.QOS2_MSG_FUN.apply(clientId));
        return mapCache.containsKey(messageId);
    }

    /**
     * 去除标识符存储
     */
    public void remove(String clientId, int messageId) {
        redissonClient.getMapCache(BorkerKey.QOS2_MSG_FUN.apply(clientId)).remove(messageId);
    }
}
