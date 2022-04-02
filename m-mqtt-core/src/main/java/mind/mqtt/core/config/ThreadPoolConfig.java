package mind.mqtt.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author qiding
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(20);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("Thread-A-");
        /*
        todo Set the rejection strategy. When the RejectedExecutionException exception is thrown, the rejectedExecution method
         caller's operating strategy is called to implement an adjustment mechanism. This strategy neither abandons the task nor
          explodes the exception, but returns the task to the caller, thereby reducing Flow of new tasks
        */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
}
