package mind.mqtt.broker.config;

import mind.model.config.BrokerProperties;
import mind.model.config.RedissonProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 加载yml配置
 *
 * @author qiding
 */
@Configuration
public class BrokerConfig {

    @Bean
    @ConfigurationProperties(prefix = BrokerProperties.PREFIX)
    public BrokerProperties getBrokerProperties() {
        return new BrokerProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = RedissonProperties.PREFIX)
    public RedissonProperties getRedissonProperties() {
        return new RedissonProperties();
    }

}
