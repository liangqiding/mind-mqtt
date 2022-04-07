package mind.mqtt.core.handler.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * mqtt消息业务逻辑处理
 *
 * @author qiding
 */
public interface MqttProcess {

    /**
     * mqtt消息处理
     *
     * @param ctx         ctx
     * @param mqttMessage mqtt消息
     */
    void process(ChannelHandlerContext ctx, MqttMessage mqttMessage);

}
