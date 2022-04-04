package mind.mqtt.core.retry;

import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.model.entity.Message;
import mind.mqtt.store.ChannelManage;

import java.util.concurrent.*;

/**
 * 发布任务
 *
 * @author qiding
 */
@Slf4j
public abstract class AbstractMqttTask {

    /**
     * 计算默认线程池大小，参考Netty
     */
    private static final int CORE_POOL_SIZE = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors()));

    /**
     * 任务线程池
     */
    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(CORE_POOL_SIZE);

    /**
     * 任务标志，为空时任务结束
     */
    private static final ConcurrentHashMap<String, Integer> TASK = new ConcurrentHashMap<>();

    /**
     * 最大重发次数，超过了该次数还不成功，则放弃重发
     */
    private static final int MAX_RESEND = 10;

    /**
     * 初始重发延时，秒
     */
    private static final int TIMEOUT = 10;

    /**
     * 每重试一次，时间间隔增加
     */
    private static final int STET = 5;

    /**
     * 业务实现
     *
     * @param message 发送逻辑
     */
    abstract protected void publish(Message message);

    /**
     * 自定义拼接延时任务的Id，不可重复，通过此Id结束任务
     *
     * @param clientId  客户端id
     * @param messageId 消息id
     * @return id
     */
    abstract String taskId(String clientId, int messageId);

    /**
     * 创建延时任务，在delay秒后执行
     *
     * @param message 要发送的消息
     * @param delay   延时（秒）
     */
    private void newSchedule(Message message, long delay) {
        EXECUTOR.schedule(() -> {
            // 超出最大重发次数
            if (delay / STET > MAX_RESEND) {
                return;
            }
            // 该标志是否还存在
            if (!TASK.containsKey(this.taskId(message.getToClientId(), message.getMessageId()))) {
                return;
            }
            // 执行发送逻辑
            this.publish(message);
            // 继续创建下一个延时任务
            this.newSchedule(message, delay + STET);
        }, delay, TimeUnit.SECONDS);
    }

    /**
     * 开始定时循环发送
     *
     * @param message 封装的消息
     */
    public void start(Message message) {
        // 先发送一次
        this.publish(message);
        // 接下来循环发
        log.info("循环发送执行{},{}", message.getToClientId(), message.getMessageId());
        // 若已存在该任务，则取消
        if (TASK.containsKey(this.taskId(message.getToClientId(), message.getMessageId()))) {
            return;
        }
        // 标志任务
        TASK.put(this.taskId(message.getToClientId(), message.getMessageId()), 1);
        // 创建延时任务
        this.newSchedule(message, TIMEOUT);
    }

    /**
     * 停止发送
     *
     * @param clientId  客户端id
     * @param messageId 消息id
     */
    public void stop(String clientId, int messageId) {
        TASK.remove(this.taskId(clientId, messageId));
    }
}
