package mind.mqtt.client.server;


import io.netty.bootstrap.Bootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.client.channel.ChannelInit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;


/**
 * 启动 Broker
 *
 * @author qiding
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MqttClient implements IMqttClient {

    private final ChannelInit channelInit;

    private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup();

    public static SocketChannel socketChannel;

    @Value("${mqtt.client.host}")
    private String clientHost;

    @Value("${mqtt.client.port}")
    private Integer clientPort;

    @Value("${mqtt.broker.host}")
    private String brokerHost;

    @Value("${mqtt.broker.port}")
    private Integer brokerPort;

    @Override
    public void start() throws Exception {
        log.info("初始化 MQTT Client ...");
        this.mqttClient();
    }

    /**
     * mqttBroker初始化
     */
    private void mqttClient() throws Exception {
        try {
            Bootstrap option = new Bootstrap()
                    .remoteAddress(clientHost, clientPort)
                    .handler(channelInit)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128);
            option.group(WORKER_GROUP);
            ChannelFuture future = option.connect(brokerHost, brokerPort).sync();
            if (future.isSuccess()) {
                socketChannel = (SocketChannel) future.channel();
                log.info("connect server success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            WORKER_GROUP.shutdownGracefully();
        }
    }

    /**
     * 销毁
     */
    @PreDestroy
    @Override
    public void destroy() {
        WORKER_GROUP.shutdownGracefully();
        socketChannel.closeFuture();
    }

}
