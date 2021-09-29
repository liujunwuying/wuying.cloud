package com.wuying.cloud.mybatis.support;

import com.wuying.cloud.mybatis.support.interceptor.MybatisRowLimitInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-24
 */

@Configuration
public class MybatisSupportConfiguration {

    @Bean
    @ConditionalOnProperty(value = "wuying.cloud.mybatis.rowLimit.enabled", matchIfMissing = true)
    MybatisRowLimitInterceptor mybatisRowLimitInterceptor() {
        return new MybatisRowLimitInterceptor();
    }
}
