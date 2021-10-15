package com.wuying.cloud.transaction.async.proxy;

import com.wuying.cloud.transaction.async.domain.Transaction;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * cglib代理类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public class CglibProxy extends AbstractTransactionProxy implements MethodInterceptor {

    private final Enhancer enhancer = new Enhancer();

    /**
     * 需要代理的方法前缀
     */
    private final String[] methodPrefixArr;

    public CglibProxy(Object readSubject, Class<?> beanType, Transaction transaction, String... methodPrefixArr) {
        super(readSubject, beanType, transaction);
        this.methodPrefixArr = methodPrefixArr;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (isProxyMethod(method.getName()) && isFeignMethod(method)) {
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

    /**
     * 判断方法是否需要代理
     * @param methodName 方法名
     * @return boolean
     */
    private boolean isProxyMethod(String methodName) {
        if (this.methodPrefixArr != null && this.methodPrefixArr.length > 0) {
            for(String methodPrefix : methodPrefixArr) {
                if (methodName.startsWith(methodPrefix)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 是否feign方法
     * @param method 方法对象
     * @return
     */
    private boolean isFeignMethod(Method method) {
        return method.isAnnotationPresent(RequestMapping.class)
                || method.isAnnotationPresent(PostMapping.class)
                || method.isAnnotationPresent(GetMapping.class)
                || method.isAnnotationPresent(DeleteMapping.class)
                || method.isAnnotationPresent(PutMapping.class);
    }
}
