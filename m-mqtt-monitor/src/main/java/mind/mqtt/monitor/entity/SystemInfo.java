package mind.mqtt.monitor.entity;

import cn.hutool.system.oshi.CpuInfo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author qiding
 */
@Data
@Accessors(chain = true)
public class SystemInfo {

    /**
     * 可用内存
     */
    private Long memoryAvailable;

    /**
     * 总内存
     */
    private Long memoryTotal;

    /**
     * 内存利用率
     */
    private String memoryUsage;

    /**
     * 系统名
     */
    private String systemName;

    /**
     * cpu使用情况
     */
    private CpuInfo cpuInfo;



}
