package mind.mqtt.broker.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import mind.mqtt.handler.MqttExceptionHandler;
import mind.mqtt.handler.MqttMessageHandler;
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
    private final MqttExceptionHandler mqttExceptionHandler;


    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline()
                // 添加心跳
                .addLast("idleStateHandler", new IdleStateHandler(10, 0, 0))
                // 添加mqtt解码器
                .addLast("mqttDecoder", new MqttDecoder())
                // 添加mqtt编码器
                .addLast("mqttEncoder", MqttEncoder.INSTANCE)
                // 添加mqtt消息处理器
                .addLast(mqttMessageHandler.getClass().getSimpleName(), mqttMessageHandler)
                // 添加异常处理器
                .addLast(mqttExceptionHandler.getClass().getSimpleName(), mqttExceptionHandler);
    }


}


