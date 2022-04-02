package mind.mqtt.core.dispatcher;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.entity.Message;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * 发布任务，确保qos1 和qos2 发布成功
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(-99)
public class PublishTask {

    private final RedissonClient redissonClient;

    @PostConstruct
    public void init() {
        new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(5), new ThreadPoolExecutor.AbortPolicy())
                .execute(() -> {
                    try {
                        test();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void test() throws InterruptedException {
        RBlockingQueue<Message> testQueue = redissonClient.getBlockingQueue("testQueue");
        while (true) {
            Message take = testQueue.take();
            log.debug("==============={}", take);
        }
    }

    public void addDelayedQueue(Message message) throws InterruptedException {
        RBlockingQueue<Object> testQueue = redissonClient.getBlockingQueue("testQueue");
        redissonClient.getDelayedQueue(testQueue).offer(message, 10, TimeUnit.SECONDS);
    }
}
