package mind.mqtt.broker.server;

import javax.annotation.PreDestroy;

/**
 * @author qiding
 */
public interface IBrokerServer {


    /**
     * 主启动程序，初始化参数
     *
     * @throws Exception 初始化异常
     */
    void start() throws Exception;

    /**
     * 优雅的结束服务器
     */
    @PreDestroy
    void destroy() throws InterruptedException;
}
