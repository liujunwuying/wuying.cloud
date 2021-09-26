package com.wuying.cloud.transaction.async;

import com.wuying.cloud.transaction.async.interceptor.GetGxidInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 装配类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
@EnableAsync
@EnableScheduling
@Configuration
@ComponentScan({"com.wuying.cloud.transaction.async"})
public class TransactionAsyncConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GetGxidInterceptor()).addPathPatterns(new String[]{"/**"});
    }

    @Bean
    @LoadBalanced
    @ConditionalOnMissingBean({RestTemplate.class})
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
