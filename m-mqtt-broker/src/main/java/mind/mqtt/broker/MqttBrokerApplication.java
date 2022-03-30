package mind.mqtt.broker;

import lombok.RequiredArgsConstructor;
import mind.mqtt.broker.server.IBrokerServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 多模块扫描所有 mind 开头的包,注入bean
 *
 * @author qiding
 */
@SpringBootApplication(scanBasePackages = {"mind"})
@RequiredArgsConstructor
@EnableScheduling
public class MqttBrokerApplication implements ApplicationRunner {

    private final IBrokerServer iBrokerServer;

    public static void main(String[] args) {
        SpringApplication.run(MqttBrokerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        iBrokerServer.start();
    }
}
