package m.mqtt.broker.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 服务配置
 *
 * @author qiding
 */
@Component
@Accessors(chain = true)
@NoArgsConstructor
@ConfigurationProperties(prefix = BrokerProperties.PREFIX)
@Data
public class BrokerProperties {

    public static final String PREFIX = "mind.mqtt.broker";

    @PostConstruct
    public void test() {
        System.out.println("==========" + this.id);
    }

    /**
     * Broker唯一标识
     */
    private String id;

    /**
     * 启动的IP地址
     */
    private String host;

    /**
     * SSL端口号
     */
    private Integer port;

    /**
     * 是否开启集群
     */
    private Boolean clusterEnabled;

    /**
     * WebSocket端口号
     */
    private Integer webSocketPort;

    /**
     * WebSocket启动的IP地址
     */
    private Boolean webSocketEnabled;

    /**
     * WebSocket 路径
     */
    private String webSocketPath;

    /**
     * SSL是否启用
     */
    private Boolean sslEnabled;


    /**
     * SSL密钥文件密码
     */
    private String sslPassword;

    /**
     * 心跳时间(秒), 默认60秒, 该值可被客户端连接时相应配置覆盖
     */
    private Integer keepAlive;

    /**
     * 是否开启Epoll模式,linux上使用EpollEventLoopGroup会有较少的gc有更高级的特性，性能更好
     */
    private Boolean useEpoll;

    /**
     * Socket参数, 存放已完成三次握手请求的队列最大长度, 默认511长度
     */
    private Integer soBacklog;

    /**
     * Socket参数, 是否开启心跳保活机制, 默认开启
     */
    private Boolean soKeepAlive;

    /**
     * 转发kafka主题
     */
    private String producerTopic;

    /**
     * MQTT.Connect消息必须通过用户名密码验证
     */
    private Boolean mqttPasswordMust;

    /**
     * 是否启用kafka消息转发
     */
    private Boolean kafkaBrokerEnabled;

}
