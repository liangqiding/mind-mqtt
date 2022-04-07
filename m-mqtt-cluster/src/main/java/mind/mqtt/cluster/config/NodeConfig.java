package mind.mqtt.cluster.config;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.model.config.BrokerProperties;
import mind.mqtt.cluster.node.NodeManage;
import mind.mqtt.store.config.BorkerKey;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * 集群节点注册
 *
 * @author qiding
 */
@RequiredArgsConstructor
@Slf4j
@Configuration
public class NodeConfig {

    private final NodeManage nodeManage;

    private final BrokerProperties brokerProperties;

    public static String nodeName;

    @PostConstruct
    public void init() {
        this.register();
    }

    /**
     * 注册集群节点
     */
    public void register() {
        String brokerId = brokerProperties.getId();
        // 判断节brokerId是否存在,已存在则重组节点id
        this.checkBrokerIdAndRename(brokerId);
        // 节点信息存储，这里可以自定义
        JSONObject nodeInfo = new JSONObject();
        nodeInfo.put("startTime", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        nodeInfo.put("ip", brokerProperties.getHost());
        nodeInfo.put("port", brokerProperties.getPort());
        nodeManage.add(nodeName, nodeInfo);
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        nodeManage.remove(nodeName);
    }

    /**
     * 判断节brokerId是否存在,已存在则重组节点id
     */
    public void checkBrokerIdAndRename(final String brokerId) {
        nodeName = brokerId;
        int i = 2;
        while (nodeManage.containsNode(nodeName)) {
            nodeName = brokerId + i;
            i++;
        }
    }

}
