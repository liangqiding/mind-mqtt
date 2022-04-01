package mind.model.entity;

import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * mqtt消息实体，用于消息存储、sql备份等
 *
 * @author qiding
 */
@Data
@Accessors(chain = true)
public class Message implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * 事件触发所在节点
     */
    private String brokerId;

    /**
     * MQTT 消息 ID
     */
    private Integer packetId;

    /**
     * 消息来源 客户端 id
     */
    private String fromClientId;

    /**
     * 消息来源 用户名
     */
    private String fromUsername;

    /**
     * topic
     */
    private String topic;

    /**
     * 消息类型
     */
    private MqttMessageType messageType;

    /**
     * 是否重发
     */
    private boolean dup;

    /**
     * qos
     */
    private int qos;

    /**
     * retain
     */
    private boolean retain;

    /**
     * 消息内容
     */
    private byte[] messageBytes;

    /**
     * 存储的时间
     */
    private long timestamp;

    /**
     * 有效时间
     */
    private long expireTime;

    /**
     * PUBLISH 消息到达 Broker 的时间 (ms)
     */
    private Long publishReceivedAt;
}
