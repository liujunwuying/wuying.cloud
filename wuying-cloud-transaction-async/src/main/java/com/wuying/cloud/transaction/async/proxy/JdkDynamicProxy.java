package com.wuying.cloud.transaction.async.proxy;

import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.enums.TransactionLevel;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk动态代理类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public class JdkDynamicProxy extends AbstractTransactionProxy implements InvocationHandler {

    public JdkDynamicProxy(Object realSubject, Transaction transaction) {
        super(realSubject, transaction);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        if (transaction.getLevel() == TransactionLevel.none) {
            return method.invoke(this.realSubject, args);
        } else {
            return intercept(proxy, method, args);
        }
    }

    @Override
    public Object generateProxy() {
        Assert.notNull(realSubject, "the real object can't be null");
        return Proxy.newProxyInstance(realSubject.getClass().getClassLoader(), realSubject.getClass().getInterfaces(), this);
    }
}
