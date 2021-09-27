package com.wuying.cloud.context;

import com.wuying.cloud.context.holder.ApplicationContextHolder;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 上下文配置类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-26
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class WuyingContextConfiguration {

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }
}
