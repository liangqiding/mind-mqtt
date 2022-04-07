package mind.mqtt.client.retry;

import mind.model.entity.Message;

/**
 * mqtt报文发送重发管理器
 *
 * @author qiding
 */
public interface IMqttRetryTask {

    /**
     * 开始延时重发
     *
     * @param message 封装的消息
     */
    void start(Message message);

    /**
     * 停止重试
     *
     * @param messageId 消息id
     */
    void stop(int messageId);
}
