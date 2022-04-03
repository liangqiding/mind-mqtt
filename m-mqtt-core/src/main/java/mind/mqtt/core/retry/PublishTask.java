package mind.mqtt.core.retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.model.entity.Message;
import mind.mqtt.store.ChannelManage;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;


/**
 * qos2重发计划，确保发布完成
 *
 * @author qiding
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PublishTask extends AbstractMqttTask {

    /**
     * 任务标志，为空时任务结束
     * clientId,messageId,预留
     */
    public static final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Integer>> TASK_MAP = new ConcurrentHashMap<>();

    /**
     * 最大重发次数，超过了该次数还不成功，则放弃重发
     */
    public static final int MAX_RESEND = 10;

    /**
     * 初始重发延时，秒
     */
    public static final int TIMEOUT = 10;

    /**
     * 每重试一次，时间间隔增加
     */
    public static final int STET = 5;


    @Override
    void publishTask(Message message, long delay) {
        // 大于最大重试次数，结束
        if (delay / STET > MAX_RESEND) {
            return;
        }
        ConcurrentHashMap<Integer, Integer> taskMap = this.getMap(message.getToClientId());
        // 不包含该消息，结束
        if (!taskMap.containsKey(message.getMessageId())) {
            return;
        }
        // 发送publish消息
        ChannelManage.sendByCid(message.getToClientId(), MqttMessageBuilder.newMqttPublishMessage(message));
        // 继续添加下一次的延时发送
        super.addSchedule(message, delay + STET);
    }

    /**
     * 开始定时循环发送
     *
     * @param message 封装的消息
     */
    public void start(Message message) {
        // 先发送一次
        ChannelManage.sendByCid(message.getToClientId(), MqttMessageBuilder.newMqttPublishMessage(message));
        // 接下来循环发，直到QOS2 3步握手完成
        log.info("qos2 循环发送执行{},{}", message.getToClientId(), message.getMessageId());
        ConcurrentHashMap<Integer, Integer> taskMap = this.getMap(message.getToClientId());
        if (taskMap.containsKey(message.getMessageId())) {
            return;
        }
        taskMap.put(message.getMessageId(), 1);
        super.addSchedule(message, TIMEOUT);
    }

    /**
     * 停止发送
     *
     * @param clientId  接收的客户端id
     * @param messageId 消息id
     */
    public void stop(String clientId, int messageId) {
        log.info("停止发送{}，{}", clientId, messageId);
        ConcurrentHashMap<Integer, Integer> taskMap = this.getMap(clientId);
        taskMap.remove(messageId);
    }

    /**
     * 获取客户端的消息map，不存在则初始化
     *
     * @param clientId 接收的客户端id
     */
    private ConcurrentHashMap<Integer, Integer> getMap(String clientId) {
        return TASK_MAP.computeIfAbsent(clientId, (key) -> new ConcurrentHashMap<>(2));
    }
}
