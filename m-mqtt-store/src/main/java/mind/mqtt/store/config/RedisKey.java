package mind.mqtt.store.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.config.BrokerProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.function.Function;
import java.util.function.UnaryOperator;


/**
 * redis中的缓存key
 *
 * @author qiding
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("ALL")
public class RedisKey {

    private final BrokerProperties brokerProperties;

    private static String brokerId;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        PREFIX = ROOT + brokerProperties.getId();
        SESSION_KEY = PREFIX + SESSION;
        SUB_KEY = PREFIX + SUBSCRIBED;
        RETAIN_MSG_KEY = PREFIX + RETAIN_MSG;
    }

    /**
     * 集合名称常量
     */
    public final static String SESSION = "sessionMap";
    public final static String SUBSCRIBED = "subMap";
    public final static String CLIENT_SUB = "clientSubSet";
    public final static String RETAIN_MSG = "retainMsgMap";
    public final static String QOS2_MSG = "qos2MsgMap";

    /**
     * mqtt缓存的根节点
     */
    public final static String ROOT = "mind-mqtt:";

    /**
     * broker缓存前缀，集群中用于区别本程序缓存
     */
    public static String PREFIX;

    /**
     * session的缓存key
     */
    public static String SESSION_KEY;

    /**
     * 已订阅的缓存key
     */
    public static String SUB_KEY;

    /**
     * 消息存储
     */
    public static String RETAIN_MSG_KEY;


    /**
     * client已订阅的缓存key，用于客户端离线，快速清除订阅
     */

    public static final Function<String, String> CLI_SUB_KEY_FUN = clientId -> PREFIX + CLIENT_SUB + ":" + clientId;

    /**
     * QOS2消息存储
     */
    public static final UnaryOperator<String> QOS2_MSG_FUN = clientId -> PREFIX + QOS2_MSG + ":" + clientId;

}
