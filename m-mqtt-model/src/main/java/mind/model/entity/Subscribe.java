package mind.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订阅实体
 *
 * @author qiding
 */
@Data
@Accessors(chain = true)
public class Subscribe implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * topic
     */
    private String topicFilter;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * mqttQoS
     */
    private int mqttQoS;

}
