package mind.mqtt.store.redis;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description : redisson配置
 *
 * @author : qiDing
 * date: 2020-12-14 09:27
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ConfigurationProperties(prefix = RedissonProperties.PREFIX)
@Component
public class RedissonProperties {

    public static final String PREFIX = "redisson";
    /**
     * 连接超时，单位：毫秒
     */
    private Integer connectTimeout;

    /**
     * 密码
     */
    private String password;

    /**
     * 服务器地址
     */
    private String address;

    /**
     * 数据库序号,只有单机模式下生效
     */
    private Integer database;

    /**
     * 传输模式 linux上开启会有更高的性能
     */
    private Boolean useEpoll;

}
