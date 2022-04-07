package mind.mqtt.client.handler.protocol.impl;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.mqtt.client.handler.protocol.MqttProcess;
import mind.mqtt.client.server.MqttClient;
import org.springframework.stereotype.Service;

/**
 * 客户端接收消息报文
 *
 * @author qiding
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PublishProcess implements MqttProcess {

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttPublishMessage pubMsg = (MqttPublishMessage) mqttMessage;
        String topic = pubMsg.variableHeader().topicName();
        byte[] bytes = ByteBufUtil.getBytes(pubMsg.payload());
        MqttQoS mqttQoS = pubMsg.fixedHeader().qosLevel();
        int packetId = pubMsg.variableHeader().packetId();
        log.debug("收到消息,topic:{},{}", topic, new String(bytes, CharsetUtil.UTF_8));
        switch (mqttQoS) {
            case AT_MOST_ONCE:
                // 转发到其它subscriber
                break;
            case AT_LEAST_ONCE:
                MqttClient.socketChannel.writeAndFlush(MqttMessageBuilder.newMqttPubAckMessage(packetId));
                break;
            case EXACTLY_ONCE:
                log.debug("qos2回复REC");
                MqttClient.socketChannel.writeAndFlush(MqttMessageBuilder.newMqttPubRecMessage(packetId));
                break;
            default:
                log.error("不支持的qos等级");
        }
    }

}
