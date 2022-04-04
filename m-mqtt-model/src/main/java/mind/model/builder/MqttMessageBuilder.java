package mind.model.builder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import mind.model.entity.Message;
import java.util.List;

/**
 * mqtt消息构建
 *
 * @author qiding
 */
public class MqttMessageBuilder {

    private static final MqttMessage PING_REG_MEG = MqttMessageFactory.newMessage(
            new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);

    /**
     * 生成 MqttPublishMessage 消息
     *
     * @param message 通用消息实体
     */
    public static MqttPublishMessage newMqttPublishMessage(Message message) {
        byte[] messageBytes = message.getMessageBytes();
        ByteBuf payload;
        if (messageBytes == null) {
            payload = Unpooled.buffer(0);
        } else {
            payload = Unpooled.wrappedBuffer(message.getMessageBytes());
        }
        return (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.valueOf(message.getMessageType()), message.isDup(), MqttQoS.valueOf(message.getQos()), message.isRetain(), 0),
                new MqttPublishVariableHeader(message.getTopic(), message.getMessageId()), payload);
    }

    /**
     * PubAck 报文
     *
     * @param messageId 消息id
     */
    public static MqttPubAckMessage newMqttPubAckMessage(int messageId) {
        return (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
    }

    /**
     * PubRec 报文
     *
     * @param messageId 消息id
     */
    public static MqttMessage newMqttPubRecMessage(int messageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
    }

    /**
     * 响应报文
     *
     * @param connectReturnCode 响应码
     */
    public static MqttConnAckMessage newMqttConnAckMessage(MqttConnectReturnCode connectReturnCode) {
        return (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(connectReturnCode, false), null);
    }

    /**
     * PingReg报文，响应PING
     */
    public static MqttMessage getPingRegMeg() {
        return PING_REG_MEG;
    }

    public static MqttMessage newMqttPubRelMessage(int messageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
    }

    /**
     * qos3 完成
     */
    public static MqttMessage newMqttPubCompMessage(int messageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
    }

    /**
     * 订阅响应消息
     */
    public static MqttSubAckMessage newMqttSubAckMessage(int messageId, List<Integer> mqttQosList) {
        return (MqttSubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                new MqttSubAckPayload(mqttQosList));
    }

    /**
     * 取消订阅响应消息
     */
    public static MqttUnsubAckMessage newMqttUnsubAckMessage(int messageId) {
        return (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
    }
}
