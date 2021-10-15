package com.wuying.cloud.transaction.async.holder;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * 事件发布holder
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
@Component
public class ApplicationEventPublisherHolder implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
         eventPublisher =applicationEventPublisher;
    }

    public static ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }
}

