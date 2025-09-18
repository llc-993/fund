package com.fund.conf

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableAsync
class ThreadPoolConfig {

    /**
     * 股票数据处理线程池
     * 用于并发处理股票数据，提高数据加载效率
     */
    @Bean("threadPoolTaskExecutor")
    fun threadPoolTaskExecutor(): ThreadPoolTaskExecutor {
        var executor = ThreadPoolTaskExecutor()
        
        // 核心线程数：根据CPU核心数设置，通常设置为CPU核心数的2倍
        val corePoolSize = Runtime.getRuntime().availableProcessors() * 2
        executor.corePoolSize = corePoolSize
        
        // 最大线程数：核心线程数的2倍
        executor.maxPoolSize = corePoolSize * 2
        
        // 队列容量：设置较大的队列容量以处理突发的大量数据
        executor.queueCapacity = 1000
        
        // 线程名前缀：便于调试和监控
        executor.setThreadNamePrefix("stock-processor-")
        
        // 线程空闲时间：超过核心线程数的线程在空闲60秒后被回收
        executor.keepAliveSeconds = 60
        
        // 拒绝策略：当线程池和队列都满时，由调用线程执行任务
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true)
        
        // 等待时间：最多等待30秒
        executor.setAwaitTerminationSeconds(30)
        
        executor.initialize()
        return executor
    }
}
