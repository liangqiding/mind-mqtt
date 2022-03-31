package mind.mqtt.monitor.system;

import cn.hutool.system.oshi.OshiUtil;
import mind.model.entity.SystemInfo;
import oshi.hardware.GlobalMemory;
import java.text.DecimalFormat;

/**
 * @author qiding
 */
public class SystemInfoUtils {

    public static SystemInfo getSystemInfo() {
        GlobalMemory memory = OshiUtil.getMemory();
        DecimalFormat df = new DecimalFormat("0.00");
        return new SystemInfo()
                .setMemoryAvailable(memory.getAvailable())
                .setMemoryTotal(memory.getTotal())
                .setMemoryUsage(df.format((memory.getTotal()-memory.getAvailable())*100 / (double) memory.getTotal()))
                .setCpuInfo(OshiUtil.getCpuInfo())
                .setSystemName(OshiUtil.getOs().toString());
    }
}
