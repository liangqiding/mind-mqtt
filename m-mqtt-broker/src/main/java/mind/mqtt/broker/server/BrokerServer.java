package mind.mqtt.broker.server;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.broker.channel.ChannelInit;
import mind.mqtt.broker.config.BrokerProperties;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 启动 Broker
 *
 * @author qiding
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BrokerServer implements IBrokerServer {

    private final BrokerProperties brokerProperties;

    private final ChannelInit channelInit;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    @Override
    public void start() throws Exception {
        log.info("Initializing {} MQTT Broker ...", JSON.toJSONString(brokerProperties));
        bossGroup = brokerProperties.getUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        workerGroup = brokerProperties.getUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        mqttBroker();
    }

    /**
     * netty初始配置
     */
    private void mqttBroker() throws Exception {
        String address = InetAddress.getLocalHost().getHostAddress();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(brokerProperties.getUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(brokerProperties.getPort()))
                    // 配置 编码器、解码器、业务处理
                    .childHandler(channelInit)
                    // tcp缓冲区
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 将网络数据积累到一定的数量后,服务器端才发送出去,会造成一定的延迟。希望服务是低延迟的,建议将TCP_NODELAY设置为true
                    .childOption(ChannelOption.TCP_NODELAY,false)
                    // 保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口，开始接收进来的连接
            ChannelFuture future = bootstrap.bind().sync();
            log.info("mqttServer启动成功！开始监听端口：{}", brokerProperties.getPort());
            // 关闭channel和块，直到它被关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 销毁
     */
    @Override
    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.warn("Shutdown succeed 已释放brokerId为 {} 的所有缓存  ...", "[" + brokerProperties.getId() + "]");
    }

}
