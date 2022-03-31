package mind.mqtt.monitor.schedule.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.monitor.schedule.ScheduleService;
import mind.mqtt.monitor.system.SystemInfoUtils;
import mind.model.entity.SystemInfo;
import mind.mqtt.store.channel.ChannelStore;
import mind.mqtt.store.mqttStore.impl.MqttSessionStore;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

/**
 * mind-links管家,控制台打印服务器健康状态
 *
 * @author qiding
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MqttLoggerImpl implements ScheduleService {

    private final MqttSessionStore mqttSessionStore;

    @Override
    public void everyMinute() {
        log.info("\n");
        this.connectLog();
    }

    public void connectLog() {
        SystemInfo systemInfo = SystemInfoUtils.getSystemInfo();
        log.info("┌———————————————————mind-links管家———————————————————————————");
        log.info("├ 当前服务器连接数为：{}", ChannelStore.getLocalConnectCount());
        log.info("├ 异常断开数为：{}", 0);
        log.info("├ 集群总连接数为：{}", mqttSessionStore.getAllNodeSessionCount());
        log.info("├——————————————————————系统信息——————————————————————————————");
        log.info("├ 系统：{}", systemInfo.getSystemName());
        log.info("├ 当前可用内存：{}MB", systemInfo.getMemoryAvailable() / 1024 / 1024);
        log.info("├ 总内存：{}MB", systemInfo.getMemoryTotal() / 1024 / 1024);
        log.info("├ 当前内存使用率：{}%", systemInfo.getMemoryUsage());
        log.info("├ cpu型号：{}", systemInfo.getCpuInfo().getCpuModel().substring(0, systemInfo.getCpuInfo().getCpuModel().indexOf("\n")));
        log.info("├ cpu核心数：{}", systemInfo.getCpuInfo().getCpuNum());
        log.info("├ 当前cpu使用率：{}%", Double.parseDouble(new DecimalFormat("#.00").format((100 - systemInfo.getCpuInfo().getFree()))));
        log.info("└——————————————————————————————————————————————————————————");
    }
}
