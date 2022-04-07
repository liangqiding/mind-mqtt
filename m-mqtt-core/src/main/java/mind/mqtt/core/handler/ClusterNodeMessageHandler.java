package mind.mqtt.core.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.entity.Message;
import mind.mqtt.cluster.consumer.AbstractMqttMessageListener;
import mind.mqtt.core.dispatcher.MqttMessageDispatcher;
import org.springframework.stereotype.Component;

/**
 * 集群中消息交互,接收其它节点发来的消息
 *
 * @author qiding
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ClusterNodeMessageHandler extends AbstractMqttMessageListener {

    private final MqttMessageDispatcher mqttMessageDispatcher;

    @Override
    protected void messageListener(String nodeId, Message message) {
        mqttMessageDispatcher.publish(message);
    }
}
