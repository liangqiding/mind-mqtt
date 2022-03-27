package mind.mqtt.broker;

import mind.mqtt.broker.server.IBrokerServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.annotation.Resource;

/**
 *
 * 多模块扫描所有 mind 开头的包,注入bean
 *
 * @author qiding
 */
@SpringBootApplication(scanBasePackages = {"mind"})
public class MqttBrokerApplication implements ApplicationRunner {

    @Resource
    private IBrokerServer iBrokerServer;

    public static void main(String[] args) {
        SpringApplication.run(MqttBrokerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        iBrokerServer.start();
    }
}
