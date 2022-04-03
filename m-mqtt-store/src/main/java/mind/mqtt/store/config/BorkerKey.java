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
public class BorkerKey {

    private final BrokerProperties brokerProperties;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        BROKER_ID = brokerProperties.getId();
        PREFIX = ROOT + BROKER_ID;
        SESSION_KEY = PREFIX + SESSION;
        SUB_KEY = PREFIX + SUBSCRIBED;
        RETAIN_MSG_KEY = PREFIX + RETAIN_MSG;
        PUB_DELAY_QUEUE_KEY = PREFIX + PUB_DEALY_QUEUE;
    }

    /**
     * 集合名称常量
     */
    public final static String SESSION = "sessionMap";
    public final static String SUBSCRIBED = "subMap";
    public final static String CLIENT_SUB = "clientSubSet";
    public final static String RETAIN_MSG = "retainMsgMap";
    public final static String QOS2_MSG = "qos2MsgMap";
    public final static String PUB_DEALY_QUEUE = "pubDealyQueue";

    /**
     * mqtt缓存的根节点
     */
    public final static String ROOT = "mind-mqtt:";

    /**
     * brokerId
     */
    private static String BROKER_ID;

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
     * retain消息存储
     */
    public static String RETAIN_MSG_KEY;

    /**
     * 消息延时队列
     */
    public static String PUB_DELAY_QUEUE_KEY;

    /**
     * client已订阅的缓存key，用于客户端离线，快速清除订阅
     */

    public static final Function<String, String> CLI_SUB_KEY_FUN = clientId -> PREFIX + CLIENT_SUB + ":" + clientId;

    /**
     * QOS2消息存储
     */
    public static final UnaryOperator<String> QOS2_MSG_FUN = clientId -> PREFIX + QOS2_MSG + ":" + clientId;

}
