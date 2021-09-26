package com.wuying.cloud.transaction.async.util;

import org.springframework.aop.framework.AdvisedSupport;

import java.lang.reflect.Field;

/**
 * 反射工具类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public class ReflectUtil {

    public static Object getCglibProxyTargetObject(Object proxy) {
        try {
            Field field = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
            field.setAccessible(true);
            Object dynamicAdvisedInterceptor = field.get(proxy);
            Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            return ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
