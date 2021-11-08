package com.wuying.cloud.transaction.async;

import com.wuying.cloud.transaction.async.interceptor.GetGxidInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 装配类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
@EnableScheduling
@Configuration
@ComponentScan({"com.wuying.cloud.transaction.async"})
public class TransactionAsyncConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GetGxidInterceptor()).addPathPatterns("/**");
    }
}
