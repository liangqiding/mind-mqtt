package mind.mqtt.broker.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import mind.common.constant.NettyConstant;;
import mind.model.config.BrokerProperties;
import mind.mqtt.core.handler.MqttExceptionHandler;
import mind.mqtt.core.handler.MqttMessageHandler;
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
     * 配置参数
     */
    private final BrokerProperties brokerProperties;
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
                // 添加心跳 读超时、写超时、读/写超时
                .addLast(NettyConstant.IDLE, new IdleStateHandler(0, 0, brokerProperties.getKeepAlive()))
                // 添加mqtt解码器
                .addLast("mqttDecoder", new MqttDecoder())
                // 添加mqtt编码器
                .addLast("mqttEncoder", MqttEncoder.INSTANCE)
                // 添加mqtt消息处理器
                .addLast("mqttMessageHandler", mqttMessageHandler)
                // 添加异常处理器
                .addLast("mqttExceptionHandler", mqttExceptionHandler);
    }


}


