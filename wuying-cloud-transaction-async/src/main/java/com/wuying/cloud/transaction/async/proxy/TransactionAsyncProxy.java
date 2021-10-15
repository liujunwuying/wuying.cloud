package com.wuying.cloud.transaction.async.proxy;

import com.wuying.cloud.context.holder.ApplicationContextHolder;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.enums.RetryIntervalLevel;
import com.wuying.cloud.transaction.async.holder.ThreadLocalHolder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.Assert;

/**
 * 事务代理类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public class TransactionAsyncProxy {
    private Transaction transaction;

    private TransactionAsyncProxy() {
        ThreadLocalHolder.getGxid().remove();
        StackTraceElement invokeElement = Thread.currentThread().getStackTrace()[3];
        String className = invokeElement.getClassName();
        String methodName = invokeElement.getMethodName();
        String maxRetryTime = ApplicationContextHolder.getProperty("wuying.cloud.transaction.async.maxRetryTimes", "34");
        transaction = Transaction.builder()
                .coordinator(className + "." + methodName)
                .maxRetryTimes(Integer.parseInt(maxRetryTime))
                .retryInterval(RetryIntervalLevel.quick.getInterval())
                .applicationName(ApplicationContextHolder.getServiceInstanceInfo().getServiceName())
                .build();
    }

    public static TransactionAsyncProxy create() {
        return new TransactionAsyncProxy();
    }

    public TransactionAsyncProxy maxRetryTime(int maxRetryTimes) {
        transaction.setMaxRetryTimes(maxRetryTimes);
        return this;
    }

    /**
     * 生成代理对象 只支持FeignClient
     * @param bean 原对象
     * @param methodPrefix 代理方法前缀
     * @return
     */
    public Object proxyClass(Object bean, String... methodPrefix) {
        Assert.notNull(bean, "入参为空");
        Class<?> beanType = findBeanType(bean);
        Assert.notNull(beanType, "调用类需包含FeignClient注解");
        return new CglibProxy(bean, beanType, transaction, methodPrefix).generateProxy();
    }

    /**
     * 获取调用类类型
     * @param bean 调用对象
     * @return
     */
    public Class<?> findBeanType(Object bean) {
        for(Class<?> clazz : bean.getClass().getInterfaces()) {
            if (clazz.isAnnotationPresent(FeignClient.class)) {
                return clazz;
            }
        }
        return null;
    }
}
