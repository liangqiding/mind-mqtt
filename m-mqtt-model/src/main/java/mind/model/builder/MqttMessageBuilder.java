package mind.model.builder;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import mind.model.entity.Connect;
import mind.model.entity.Message;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * mqtt消息构建
 *
 * @author qiding
 */
public class MqttMessageBuilder {

    private static final MqttMessage PING_RESP = MqttMessageFactory.newMessage(
            new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);

    private static final MqttMessage PING_REQ = MqttMessageFactory.newMessage(
            new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);

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
        return MqttMessageBuilders
                .publish()
                .messageId(message.getMessageId())
                .payload(payload)
                .topicName(message.getTopic())
                .qos(MqttQoS.valueOf(message.getQos()))
                .retained(message.isRetain())
                .build();
    }

    /**
     * PubAck 报文
     *
     * @param messageId 消息id
     */
    public static MqttMessage newMqttPubAckMessage(int messageId) {
        return MqttMessageBuilders.pubAck().packetId(messageId).build();
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
        return MqttMessageBuilders.connAck().returnCode(connectReturnCode).build();
    }

    /**
     * PingReq报文，请求PING
     */
    public static MqttMessage getPingReq() {
        return PING_REQ;
    }

    /**
     * PingResp报文，响应PING
     */
    public static MqttMessage getPingResp() {
        return PING_RESP;
    }


    /**
     * pub-rel报文
     *
     * @param messageId 消息id
     */
    public static MqttMessage newMqttPubRelMessage(int messageId) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttMessageIdVariableHeader idVariableHeader = MqttMessageIdVariableHeader.from(messageId);
        return MqttMessageFactory.newMessage(fixedHeader, idVariableHeader, null);
    }

    /**
     * PubComp报文 qos3 完成
     *
     * @param messageId 消息id
     */
    public static MqttMessage newMqttPubCompMessage(int messageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
    }

    /**
     * 订阅响应消息
     *
     * @param messageId   消息id
     * @param mqttQosList 成功或失败订阅的qos集合
     */
    public static MqttSubAckMessage newMqttSubAckMessage(int messageId, List<Integer> mqttQosList) {
        return (MqttSubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                new MqttSubAckPayload(mqttQosList));
    }

    /**
     * 取消订阅响应消息
     *
     * @param messageId 消息id
     */
    public static MqttUnsubAckMessage newMqttUnsubAckMessage(int messageId) {
        return MqttMessageBuilders.unsubAck().packetId(messageId).build();
    }

    /**
     * 连接报文
     *
     * @param connect 连接信息实体
     */
    public static MqttConnectMessage newMqttConnectMessage(Connect connect) {
        return MqttMessageBuilders.connect()
                .clientId(connect.getClientId())
                .hasUser(connect.getUsername() != null)
                .hasPassword(connect.getPassword() != null)
                .username(connect.getUsername())
                .password(connect.getPassword() == null ? new byte[0] : connect.getPassword().getBytes(StandardCharsets.UTF_8))
                .willFlag(connect.isWillFlag())
                .willTopic(connect.getWillTopic())
                .willMessage(connect.getWillPayload() == null ? new byte[0] : connect.getWillPayload().getBytes(StandardCharsets.UTF_8))
                .willQoS(MqttQoS.valueOf(connect.getWillQos()))
                .willRetain(connect.isRetain()).build();
    }
}
