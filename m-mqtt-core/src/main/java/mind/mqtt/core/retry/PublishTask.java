package mind.mqtt.core.retry;

import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.extern.slf4j.Slf4j;
import mind.model.entity.Message;

import java.util.concurrent.*;

/**
 * 发布任务
 *
 * @author qiding
 */
@Slf4j
public abstract class PublishTask {

    private static final int CORE_POOL_SIZE = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() / 2));

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(CORE_POOL_SIZE);

    /**
     * 添加定时任务，在delay秒后执行
     *
     * @param message 要发送的消息
     * @param delay   延时（秒）
     */
    public ScheduledFuture<?> addSchedule(Message message, long delay) {
        return EXECUTOR.schedule(() -> publishTask(message, delay), delay, TimeUnit.SECONDS);
    }

    public void removeSchedule(ScheduledFuture<?> timer) {
        if (timer != null) {
            timer.cancel(true);
        }
    }

    /**
     * 业务实现
     *
     * @param message 发送逻辑
     * @param delay   本次任务的延时时间
     */
    abstract void publishTask(Message message, long delay);
}
