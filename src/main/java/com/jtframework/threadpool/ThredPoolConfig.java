package com.jtframework.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableAsync
@Slf4j
public class ThredPoolConfig {

    /**
     *
     * @param corePoolSize 核心线程数量
     * @param maxPoolSize 最大线程数量
     * @param queueCapacity 等待队列长度
     * @return
     */
    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(@Value("${threadPool.corePoolSize:5}") Integer corePoolSize,
                                                         @Value("${threadPool.maxPoolSize:100}")  Integer maxPoolSize,
                                                         @Value("${threadPool.queueCapacity:1000}") Integer queueCapacity) {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();

        pool.setKeepAliveSeconds(300);
        //核心线程池数
        pool.setCorePoolSize(corePoolSize);
        //最大线程
        pool.setMaxPoolSize(maxPoolSize);
        //队列容量
        pool.setQueueCapacity(queueCapacity);
        //队列满，线程被拒绝执行策略
        pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

        pool.setThreadNamePrefix("aaaa-common-support-service--");

        log.info("正在初始化线程池:{},{},{}......",corePoolSize,maxPoolSize,queueCapacity);

        return pool;
    }

//    /**
//     * 定时任务线程池,暂时用不到
//     * @return
//     */
//    @Bean
//    public ThreadPoolTaskScheduler threadPoolTaskScheduler(@Value("${threadPool.corePoolSize:30}") Integer schedulerPoolSize) {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//
//        /**
//         * 线程池大小
//         */
//        scheduler.setPoolSize(schedulerPoolSize);
//
//        /**
//         * 线程名称前缀
//         */
//        scheduler.setThreadNamePrefix("scheduler-collector-");
//
//        scheduler.setAwaitTerminationSeconds(60);
//
//        scheduler.setWaitForTasksToCompleteOnShutdown(true);
//
//        return scheduler;
//    }
}
