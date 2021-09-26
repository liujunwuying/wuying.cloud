package com.wuying.cloud.transaction.async.proxy;

import com.wuying.cloud.transaction.async.TransactionAsyncContext;
import com.wuying.cloud.transaction.async.domain.Participant;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.enums.TransactionLevel;
import com.wuying.cloud.transaction.async.holder.ApplicationContextHolder;
import com.wuying.cloud.transaction.async.holder.ApplicationEventPublisherHolder;
import com.wuying.cloud.transaction.async.holder.ThreadLocalHolder;
import com.wuying.cloud.transaction.async.service.TransactionAsyncService;
import com.wuying.cloud.transaction.async.util.TxidGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 事务代理类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public abstract class AbstractTransactionProxy {

    protected Transaction transaction;

    protected Object realSubject;

    protected Environment environment;

    public AbstractTransactionProxy(Object realSubject, Transaction transaction) {
        this.realSubject = realSubject;
        this.transaction = transaction;
        this.environment = ApplicationContextHolder.getApplicationContext().getBean(Environment.class);
    }

    /**
     * 生成代理对象
     * @return
     */
    public abstract Object generateProxy();

    protected Object intercept(Object obj, Method method, Object[] args) throws Throwable {
        long date = System.currentTimeMillis();
        Participant p = Participant.builder()
                .bean(getBeanName(method))
                .method(method.getName())
                .param(args)
                .paramTypes(method.getParameterTypes())
                .createTime(date)
                .lastUpdateTime(date)
                .build();

        Transaction entity = Transaction.builder()
                .participant(p)
                .maxRetryTimes(transaction.getMaxRetryTimes())
                .coordinator(transaction.getCoordinator())
                .retryInterval(transaction.getRetryInterval())
                .level(transaction.getLevel())
                .applicationName(ApplicationContextHolder.getBean(Environment.class).getProperty("spring.application.name"))
                .build();

        if (args != null && args.length > 0 && args[0] instanceof TransactionAsyncContext) {
            setTxidFromTransactionContext(entity, args);
        }
        if (StringUtils.isBlank(entity.getGxid())) {
            setTxidFromThreadLocal(entity);
        }
        if (entity.getLevel() == TransactionLevel.assure) {
            ApplicationContextHolder.getBean(TransactionAsyncService.class).create(entity);
        }
        ApplicationEventPublisherHolder.getEventPublisher().publishEvent(entity);
        return convertIfNecessary(method.getReturnType());
    }

    private Object convertIfNecessary(Class<?> returnType) {
        if (returnType == Integer.TYPE || returnType == Short.TYPE
                || returnType == Long.TYPE || returnType == Character.TYPE) {
            return 0;
        } else if (returnType == Byte.TYPE) {
            return new Byte("0");
        } else if (returnType == Boolean.TYPE) {
            return Boolean.FALSE;
        } else if (returnType == Float.TYPE || returnType == Double.TYPE){
            return  0.0D;
        } else {
            return null;
        }
    }

    private void setTxidFromTransactionContext(Transaction transaction, Object[] args) {
        TransactionAsyncContext context = args[0] == null ? new TransactionAsyncContext() : (TransactionAsyncContext)args[0];
        if (StringUtils.isBlank(context.getGxid())) {
            String gxid = ThreadLocalHolder.getGxid().get();
            if (StringUtils.isNotBlank(gxid)) {
                context.setGxid(gxid);
                transaction.setTxid(TxidGenerator.generate());
            } else {
                context.setGxid(TxidGenerator.generate());
                transaction.setGxid(context.getGxid());
                ThreadLocalHolder.getGxid().set(context.getGxid());
            }
        }
    }

    private void setTxidFromThreadLocal(Transaction transaction) {
        String gxid = ThreadLocalHolder.getGxid().get();
        if (StringUtils.isNotBlank(gxid)) {
            transaction.setGxid(gxid);
            transaction.setTxid(TxidGenerator.generate());
        } else {
            transaction.setGxid(TxidGenerator.generate());
            transaction.setTxid(transaction.getGxid());
            ThreadLocalHolder.getGxid().set(transaction.getGxid());
        }
    }

    private String getBeanName(Method method) {
        String beanName = null;
        if (isFeignMethod(method)) {
            Class<?> clazz = findFeignClient(realSubject.getClass().getInterfaces());
            if (clazz != null) {
                beanName = clazz.getName();
            }
        }
        if (beanName == null) {
            beanName = StringUtils.uncapitalize(realSubject.getClass().getSimpleName());
        }
        return beanName;
    }

    private boolean isFeignMethod(Method method) {
        if (method != null) {
            Annotation[] annotations = method.getAnnotations();
            if (annotations != null && annotations.length > 0) {
                for (Annotation annotation : annotations) {
                    if (RequestMapping.class.getSimpleName().equals(annotation.annotationType().getSimpleName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Class<?> findFeignClient(Class<?>[] clazzArr) {
        if (clazzArr == null || clazzArr.length == 0) {
            return null;
        }
        for (Class<?> clazz : clazzArr) {
            Annotation[] annotations = clazz.getAnnotations();
            if (annotations != null && annotations.length > 0) {
                for(Annotation annotation : annotations) {
                    if (FeignClient.class.getSimpleName().equals(annotation.annotationType().getSimpleName())) {
                        return clazz;
                    }
                }
            }
        }
        return null;
    }
}
