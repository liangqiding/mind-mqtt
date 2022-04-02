package mind.mqtt.core.dispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.entity.Message;
import mind.mqtt.store.ChannelManage;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
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
public class PublishTask implements ApplicationRunner {

    private final RedissonClient redissonClient;

    /**
     * 标记该消息是否已完成，若存在，则表示未完成
     * clientId,messageId,重发的次数
     */
    private static final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Integer>> TASK_MAP = new ConcurrentHashMap<>();

    /**
     * 最大重发次数，超过了该次数还不成功，则放弃重发
     */
    private static final int MAX_RESEND = 10;

    /**
     * 重发间隔，秒
     */
    private static final int DELAY_TIME = 15;

    @Override
    public void run(ApplicationArguments args) {
        Executors.newScheduledThreadPool(1)
                .execute(() -> {
                    try {
                        listener();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void listener() throws InterruptedException {
        RBlockingQueue<Message> testQueue = redissonClient.getBlockingQueue("testQueue");
        while (true) {
            try {
                Message message = testQueue.take();
                log.debug("==============={}", message);
                // 假如存在，则把取出的消息发送，并继续放入延时队列直到成功
                Optional.ofNullable(TASK_MAP.get(message.getToClientId()))
                        .filter(map -> map.contains(message.getPacketId()))
                        .ifPresent(map -> {
                            Integer resend = map.computeIfPresent(message.getPacketId(), (messageId, count) -> count++);
                            // 重试超10次，则放弃
                            if (Objects.nonNull(resend) && resend > MAX_RESEND) {
                                map.remove(message.getPacketId());
                                return;
                            }
                            // 消息重发
                            ChannelManage.sendByCid(message.getToClientId(), message);
                            // 继续放回延时队列
                            this.addDelayedQueue(message);
                        });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加消息到延时队列
     */
    private void addDelayedQueue(Message message) {
        RBlockingQueue<Object> testQueue = redissonClient.getBlockingQueue("testQueue");
        redissonClient.getDelayedQueue(testQueue).offer(message, DELAY_TIME, TimeUnit.SECONDS);
    }

    /**
     * 开始循环发送
     */
    public void startPublishTask(Message message) {
        ConcurrentHashMap<Integer, Integer> resendMap = TASK_MAP.computeIfAbsent(message.getToClientId(), (key) -> new ConcurrentHashMap<>());
        resendMap.put(message.getPacketId(), 1);
        ChannelManage.sendByCid(message.getToClientId(), message);
        // 添加到延时队列，持续发送
        this.addDelayedQueue(message);
    }

    /**
     * 停止循环发送
     */
    public void stopPublishTask(String clientId, int messageId) {
        Optional.ofNullable(TASK_MAP.get(clientId))
                .ifPresent(map -> map.remove(messageId));
    }
}
