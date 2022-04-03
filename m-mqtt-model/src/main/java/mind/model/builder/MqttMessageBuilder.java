package mind.model.builder;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import mind.model.entity.Message;

/**
 * mqtt 消息构建
 *
 * @author qiding
 */
public class MqttMessageBuilder {

    /**
     * 生成 MqttPublishMessage 消息
     *
     * @param message 通用消息实体
     */
    public static MqttPublishMessage newPublishMessage(Message message) {
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

}
