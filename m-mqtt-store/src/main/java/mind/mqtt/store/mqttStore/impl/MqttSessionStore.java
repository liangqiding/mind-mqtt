package mind.mqtt.store.mqttStore.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * @author qiding
 */
@Service
@RequiredArgsConstructor
public class MqttSessionStore {

    private final RedissonClient redissonClient;

    private final static String MAP_NAME = "mind-mqtt:session";

    public void test() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("test", "我是你爹");
//        redissonClient.getMapCache(MAP_NAME).;
    }
}
