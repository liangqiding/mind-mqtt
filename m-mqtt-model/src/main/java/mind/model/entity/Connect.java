package mind.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 封装登录报文实体，用于客户端登录
 *
 * @author qiding
 */
@Data
@Accessors(chain = true)
public class Connect {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 客户端Id
     */
    private String clientId;

    /**
     * 是否有遗嘱
     */
    private boolean isWillFlag;

    /**
     * 遗嘱topic
     */
    private String willTopic;

    /**
     * 遗嘱内容
     */
    private String willPayload;

    private Integer willQos = 0;

    private boolean retain;
}
