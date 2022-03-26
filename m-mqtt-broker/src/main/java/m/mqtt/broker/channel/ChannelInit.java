package m.mqtt.broker.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import m.mqtt.broker.channel.handler.ExceptionHandler;
import m.mqtt.broker.channel.handler.MqttMessageHandler;
import org.springframework.stereotype.Component;

/**
 * Netty 通道初始化
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
public class ChannelInit extends ChannelInitializer<SocketChannel> {

    /**
     * IO处理程序
     */
    private final MqttMessageHandler mqttMessageHandler;

    /**
     * 异常处理程序
     */
    private final ExceptionHandler exceptionHandler;


    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline()
                // 添加心跳
                .addLast("idleStateHandler", new IdleStateHandler(10, 0, 0))
                .addLast("mqttDecoder", new MqttDecoder())
                // 添加mqtt解码器
                .addLast("mqttEncoder", MqttEncoder.INSTANCE)
                .addLast(mqttMessageHandler.getClass().getSimpleName(), mqttMessageHandler)
                .addLast(exceptionHandler.getClass().getSimpleName(), exceptionHandler);
    }


}


