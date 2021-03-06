package mind.mqtt.core.retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.model.entity.Message;
import mind.mqtt.store.ChannelManage;
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
        ChannelManage.sendByCid(message.getToClientId(), MqttMessageBuilder.newMqttPublishMessage(message));
    }

    @Override
    protected String taskId(String clientId, int messageId) {
        return "publish#" + clientId + "#" + messageId;
    }

}
