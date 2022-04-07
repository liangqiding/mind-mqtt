package mind.mqtt.client.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import mind.mqtt.client.handler.MqttMessageHandler;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * Netty 通道初始化
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
public class ChannelInit extends ChannelInitializer<SocketChannel> {

    private final MqttMessageHandler mqttMessageHandler;

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline()
                // 每隔5s的时间触发一次userEventTriggered的方法，并且指定IdleState的状态位是WRITER_IDLE
                .addLast("idle", new IdleStateHandler(0, 60, 0, TimeUnit.SECONDS))
                // 添加mqtt解码器
                .addLast("mqttDecoder", new MqttDecoder())
                // 添加mqtt编码器
                .addLast("mqttEncoder", MqttEncoder.INSTANCE)
                // 添加mqtt消息处理器
                .addLast("mqttMessageHandler", mqttMessageHandler);
    }

}


