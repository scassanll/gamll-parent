package com.atguigu.gmall.common.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * 配置线程池
 */
@EnableConfigurationProperties(AppThreadPoolProperties.class)
@Configuration
public class AppThreadPoolAutoConfiguration {

    @Resource
    AppThreadPoolProperties threadPoolProperties;

    @Value("${spring.application.name}")
    String applicationName;

    @Bean
    public ThreadPoolExecutor coreExecutor(){
/**
 *
 *         线程池七大参数
 *         int corePoolSize,                    核心线程数
 *         int maximumPoolSize,                 最大线程数
 *         long keepAliveTime,                  线程存活时间
 *         TimeUnit unit,                       时间单位
 *         BlockingQueue<Runnable> workQueue,   阻塞队列大小
 *         ThreadFactory threadFactory,         线程工厂 自定义创建线程的方法
 *         RejectedExecutionHandler handler)    拒绝策额
 *
 */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                threadPoolProperties.getCore(),
                threadPoolProperties.getMax(),
                threadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(threadPoolProperties.queueSize),
                new ThreadFactory() {
                    int i = 0 ;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName(applicationName+"[ core-thread-"+i++ + " ]");
                        return thread;
                }
    },
                new ThreadPoolExecutor.CallerRunsPolicy()
                );
        return executor;
    }
}
