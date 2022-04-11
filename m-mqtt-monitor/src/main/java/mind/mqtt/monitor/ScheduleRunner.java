package mind.mqtt.monitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mind.mqtt.monitor.schedule.ScheduleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 统一执行定时任务
 *
 * @author qiding
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnExpression("'${mind.mqtt.monitor.enable:true}' == 'true'")
public class ScheduleRunner {

    /**
     * 装配所有定时任务
     */
    private final List<ScheduleService> scheduleServiceList;

    /**
     * 每分钟
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void everyMinute() {
        scheduleServiceList.forEach(ScheduleService::everyMinute);
    }

    /**
     * 每五分钟
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void everyFiveMinute() {
        scheduleServiceList.forEach(ScheduleService::everyFiveMinute);
    }

    /**
     * 每小时
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void everyHour() {
        scheduleServiceList.forEach(ScheduleService::everyHour);
    }

    /**
     * 每天上午8点
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void everyDayEightClock() {
        scheduleServiceList.forEach(ScheduleService::everyDayEightClock);
    }

}
