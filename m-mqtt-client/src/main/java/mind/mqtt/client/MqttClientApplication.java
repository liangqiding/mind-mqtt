package mind.mqtt.client;

import lombok.RequiredArgsConstructor;
import mind.mqtt.client.server.IMqttClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author qiding
 */
@RequiredArgsConstructor
@EnableScheduling
@SpringBootApplication
public class MqttClientApplication implements ApplicationRunner {

    private final IMqttClient iMqttClient;

    public static void main(String[] args) {
        SpringApplication.run(MqttClientApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        iMqttClient.start();
    }
}
