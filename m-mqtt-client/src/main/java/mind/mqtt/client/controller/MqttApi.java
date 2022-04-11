package mind.mqtt.client.controller;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.builder.MqttMessageBuilder;
import mind.model.entity.Connect;
import mind.model.entity.Message;
import mind.mqtt.client.server.IMqttClient;
import mind.mqtt.client.server.MqttClient;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * 模拟发送api
 *
 * @author qiding
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class MqttApi {

    private final IMqttClient mqttClient;

    /**
     * 登录
     */
    @PostMapping("/login")
    public String login(@RequestBody Connect connect) {
        MqttClient.socketChannel.writeAndFlush(MqttMessageBuilder.newMqttConnectMessage(connect));
        return "发送成功";
    }

    /**
     * 消息发布
     */
    @PostMapping("/send")
    public String send(@RequestBody JSONObject body) {
        Message message = new Message()
                .setRetain(body.getBoolean("isRetain"))
                .setTopic(body.getString("topic"))
                .setQos(body.getInteger("qos"))
                .setMessageId(body.getInteger("messageId"))
                .setMessageBytes(body.getJSONObject("message").toJSONString().getBytes(StandardCharsets.UTF_8))
                .setMessageType(MqttMessageType.PUBLISH.value());
        MqttClient.socketChannel.writeAndFlush(MqttMessageBuilder.newMqttPublishMessage(message));
        return "发送成功";
    }

    /**
     * 订阅
     */
    @GetMapping("sub")
    public String subscribe(String topic, Integer qos) {
        MqttSubscribeMessage build = MqttMessageBuilders
                .subscribe()
                .messageId(new Random().nextInt(60000))
                .addSubscription(MqttQoS.valueOf(qos), topic)
                .build();
        MqttClient.socketChannel.writeAndFlush(build);
        return "发送成功";
    }

    /**
     * 取消订阅
     */
    @GetMapping("unSub")
    public String unSub(String topic) {
        MqttUnsubscribeMessage build = MqttMessageBuilders
                .unsubscribe()
                .messageId(new Random().nextInt(60000))
                .addTopicFilter(topic)
                .build();
        MqttClient.socketChannel.writeAndFlush(build);
        return "发送成功";
    }


    /**
     * 测试发送
     */
    @GetMapping("reconnect")
    public String reconnect() throws Exception {
        mqttClient.reconnect();
        return "重启指令发送成功";
    }

    /**
     * 测试发送
     */
    @GetMapping("test")
    public String test() {
        MqttMessage mqttMessage = MqttMessageBuilder.newMqttPubCompMessage(999);
        MqttClient.socketChannel.writeAndFlush(mqttMessage);
        return "发送成功";
    }
}
