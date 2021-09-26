package com.wuying.cloud.transaction.async.listener;

import com.wuying.cloud.transaction.async.interceptor.RestSetGxidInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 上下文刷新监听器
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    private volatile AtomicBoolean isInit = new AtomicBoolean(false);

    @Autowired(required = false)
    private RestTemplate restTemplate;

    @Autowired
    private RestSetGxidInterceptor restSetGxidInterceptor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isInit.compareAndSet(false, true)) {
            if (this.restTemplate != null && restSetGxidInterceptor != null) {
                restTemplate.getInterceptors().add(restSetGxidInterceptor);
            }
        }
    }
}
