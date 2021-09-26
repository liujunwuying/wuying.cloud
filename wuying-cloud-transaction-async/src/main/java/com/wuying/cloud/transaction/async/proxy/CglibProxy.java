package com.wuying.cloud.transaction.async.proxy;

import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.enums.TransactionLevel;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * cglib代理类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public class CglibProxy extends AbstractTransactionProxy implements MethodInterceptor {

    private Enhancer enhancer = new Enhancer();

    private String[] methodPrefixArr;

    public CglibProxy(Object readSubject, Transaction transaction, String... methodPrefixArr) {
        super(readSubject, transaction);
        this.methodPrefixArr = methodPrefixArr;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (transaction.getLevel() != TransactionLevel.none && isProxyMethod(method.getName())) {
            return intercept(obj, method, args);
        } else {
            return method.invoke(realSubject, args);
        }
    }

    @Override
    public Object generateProxy() {
        Assert.notNull(realSubject, "the real object can't be null");
        enhancer.setSuperclass(realSubject.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    private boolean isProxyMethod(String methodName) {
        if (this.methodPrefixArr != null && this.methodPrefixArr.length > 0) {
            for(String methodPrefix : methodPrefixArr) {
                if (methodName.startsWith(methodPrefix)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
