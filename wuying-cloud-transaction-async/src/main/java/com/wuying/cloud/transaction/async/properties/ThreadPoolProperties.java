package com.wuying.cloud.transaction.async.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 线程池配置项
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-24
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "wuying.cloud.transaction.async.threadpool")
public class ThreadPoolProperties {

    /**
     * 核心线程数
     */
    private int corePoolSize = 2;

    /**
     * 最大线程数
     */
    private int maximumPoolSize = 10;

    /**
     * 空闲超时时间
     */
    private long keepAliveTime = 60L;

    /**
     * 任务队列上限
     */
    private int queueSize = 10000;
}
