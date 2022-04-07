package mind.mqtt.client.retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.model.entity.Message;
import mind.mqtt.client.server.MqttClient;
import org.springframework.stereotype.Component;


/**
 * qos1、qos2 重发计划，确保发布完成
 *
 * @author qiding
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PublishTask extends AbstractMqttTask {

    @Override
    protected void publish(Message message) {
        // 发送publish消息
        MqttClient.socketChannel.writeAndFlush(MqttMessageBuilder.newMqttPublishMessage(message));
    }

    @Override
    protected String taskId(int messageId) {
        return "publish#" + messageId;
    }

}
