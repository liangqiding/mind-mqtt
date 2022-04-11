package mind.model.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务yml配置
 *
 * @author qiding
 */

@Accessors(chain = true)
@Data
public class BrokerProperties {

    public static final String PREFIX = "mind.mqtt.broker";

    /**
     * Broker唯一标识
     */
    private String id;

    /**
     * 启动的IP地址
     */
    private String host;

    /**
     * 管理员账号
     */
    private String username;

    /**
     * 管理员密码
     */
    private String password;

    /**
     * SSL端口号
     */
    private Integer port;

    /**
     * 心跳时间(秒), 默认60秒, 该值可被客户端连接时相应配置覆盖
     */
    private Integer keepAlive;

    /**
     * 是否开启Epoll模式,linux上使用EpollEventLoopGroup会有较少的gc有更高级的特性，性能更好
     */
    private Boolean useEpoll;

}
