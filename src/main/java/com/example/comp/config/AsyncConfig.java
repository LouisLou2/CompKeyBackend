package com.example.comp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

  @Value("${async.thread-pool.core-pool-size}")
  private short corePoolSize;
  @Value("${async.thread-pool.max-pool-size}")
  private short maxPoolSize;
  @Value("${async.thread-pool.queue-capacity}")
  private short queueCapacity;
  @Value("${async.thread-pool.thread-name-prefix}")
  private String threadNamePrefix;
  @Value("${async.thread-pool.keep-alive-seconds}")
  private short keepAliveSeconds;
  @Value("${async.thread-pool.await-termination-seconds}")
  private short awaitTerminationSeconds;

  @Bean(name = "taskExecutor")
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix(threadNamePrefix);
    executor.setKeepAliveSeconds(keepAliveSeconds);
    executor.setAllowCoreThreadTimeOut(true);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
    executor.initialize();
    return executor;
  }
}