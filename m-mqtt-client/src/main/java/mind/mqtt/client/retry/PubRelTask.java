package mind.mqtt.client.retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.model.entity.Message;
import mind.mqtt.client.server.MqttClient;
import org.springframework.stereotype.Component;


/**
 * PubRel报文重发计划，确保发布完成
 *
 * @author qiding
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PubRelTask extends AbstractMqttTask {

    @Override
    protected void publish(Message message) {
        // 发送PubRel消息
        log.debug("发送PubRel消息");
        MqttClient.socketChannel.writeAndFlush(MqttMessageBuilder.newMqttPubRelMessage(message.getMessageId()));
    }

    @Override
    protected String taskId(int messageId) {
        return "pubRel#" + messageId;
    }

}
