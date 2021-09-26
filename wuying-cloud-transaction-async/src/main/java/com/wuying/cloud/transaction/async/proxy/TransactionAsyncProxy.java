package com.wuying.cloud.transaction.async.proxy;

import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.enums.RetryIntervalLevel;
import com.wuying.cloud.transaction.async.enums.TransactionLevel;
import com.wuying.cloud.transaction.async.holder.ApplicationContextHolder;
import com.wuying.cloud.transaction.async.holder.ThreadLocalHolder;
import com.wuying.cloud.transaction.async.util.ReflectUtil;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.env.Environment;

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
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        Environment environment = ApplicationContextHolder.getApplicationContext().getBean(Environment.class);
        String maxRetryTime = environment.getProperty("wuying.cloud.transaction.async.maxRetryTimes", "34");
        String level = environment.getProperty("wuying.cloud.transaction.async.level", "0");
        transaction = Transaction.builder()
                .coordinator(className + "." + methodName)
                .maxRetryTimes(Integer.parseInt(maxRetryTime))
                .level(TransactionLevel.toEnum(Integer.parseInt(level)))
                .retryInterval(RetryIntervalLevel.quick.getInterval()).build();
    }

    public static TransactionAsyncProxy create() {
        return new TransactionAsyncProxy();
    }

    public TransactionAsyncProxy maxRetryTime(int maxRetryTimes) {
        transaction.setMaxRetryTimes(maxRetryTimes);
        return this;
    }

    public TransactionAsyncProxy level(TransactionLevel transactionLevel) {
        transaction.setLevel(transactionLevel);
        return this;
    }

    public Object proxyInterface(Object bean) {
        if (AopUtils.isCglibProxy(bean)) {
            bean = ReflectUtil.getCglibProxyTargetObject(bean);
        }
        JdkDynamicProxy proxy = new JdkDynamicProxy(bean, transaction);
        return proxy.generateProxy();
    }

    public Object proxyClass(Object bean, String... methodPrefix) {
        return new CglibProxy(bean, transaction, methodPrefix).generateProxy();
    }
}
