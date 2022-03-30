package mind.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * 会话实体类，存储客户端和broker的关系
 *
 * @author qiding
 */
@Data
@Accessors(chain = true)
public class MqttSession implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * mqtt服务器id
     */
    private String brokerId;

    /**
     * 用户id
     */
    private String username;

    /**
     * 有效期 单位秒
     */
    private Integer expire;

    /**
     * 管道id
     */
    private String channelId;

    /**
     * 清空session
     */
    private boolean cleanSession;

    /**
     * 是否设置遗愿,默认false
     */
    private boolean isWillFlag;

    /**
     * Mqtt遗嘱消息
     */
    private Message willMessage;

    public MqttSession() {
        this.isWillFlag = false;
    }

}
