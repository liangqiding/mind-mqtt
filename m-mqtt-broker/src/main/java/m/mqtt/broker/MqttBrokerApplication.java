package m.mqtt.broker;

import m.mqtt.broker.server.IBrokerServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.annotation.Resource;

/**
 * @author qiding
 */
@SpringBootApplication
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
