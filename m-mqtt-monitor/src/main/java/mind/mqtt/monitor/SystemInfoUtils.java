package mind.mqtt.monitor;


import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import mind.mqtt.monitor.entity.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.text.DecimalFormat;
import java.util.List;

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

    public static void main(String[] args) {
        System.out.println(OshiUtil.getCpuInfo().toString());
    }
}
