package com.wuying.cloud.transaction.async;

import com.wuying.cloud.transaction.async.domain.Participant;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.enums.TransactionStatus;
import com.wuying.cloud.transaction.async.service.AsyncStatusChangedService;
import com.wuying.cloud.transaction.async.util.ReflectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 异步确保管理类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
@Component
public class TransactionAsyncManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 重试间隔
     */
    private long initRetryInterval = 10L;

    /**
     * 延时队列上限
     */
    @Value("{wuying.cloud.transaction.async.delayQueue.maxSize:10000}")
    private int maxDelayQueueSize;

    @Autowired
    private AsyncStatusChangedService asyncStatusChangedService;

    private static volatile int counter;

    public void commit(Transaction transaction) throws InterruptedException {
        Participant participant = transaction.getParticipant();
        try {
            Object bean = applicationContext.getBean(participant.getBean());
            if (AopUtils.isCglibProxy(bean)) {
                bean = ReflectUtil.getCglibProxyTargetObject(bean);
            }
            Method method = getMethodIfAvailable(bean.getClass(), participant.getMethod(), participant.getParamTypes());
            if (method != null) {
                //new TransactionAsyncInvoker(applicationContext, participant.getBean(), method, participant.getParam(), transaction).execute();
                participant.setRetriedTimes(participant.getRetriedTimes() + 1);
                participant.setStatus(TransactionStatus.success);
                participant.setStatusText(TransactionStatus.success.getName());
                return;
            }
            logger.error("transaction fail:{}, method not exist:{}.{}", transaction.getTxid(), bean.getClass().getSimpleName(),participant.getMethod());
            participant.setStatus(TransactionStatus.fail);
            String statusText = "transaction fail, method not exist:" + bean.getClass().getSimpleName() + "." + participant.getMethod();
            participant.setStatusText(statusText);
        } catch (Exception exception) {
            String statusText = getStatusText(exception);
            participant.setRetriedTimes(participant.getRetriedTimes() + 1);
            if (participant.getRetriedTimes() < transaction.getMaxRetryTimes()) {
                logger.warn("transaction[{}]fail, waiting for retry:{} -> {}.{}", transaction.getTxid(), transaction.getCoordinator(), participant.getBean(), participant.getMethod());
                participant.setStatus(TransactionStatus.retry);
                participant.setStatusText(statusText);
                //long delayTime =
            }
        }

    }

    private Method getMethodIfAvailable(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        if (paramTypes != null) {
            try {
                return clazz.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException nme) {
                return null;
            }
        } else {
            Set<Method> candidates = new HashSet(1);
            for (Method method : clazz.getMethods()) {
                if (methodName.equals(method.getName()) && method.getParameterTypes().length == 0) {
                    candidates.add(method);
                }
            }
            if (candidates.size() == 1) {
                return candidates.iterator().next();
            } else {
                return null;
            }
        }
    }

    private String getStatusText(Exception exception) {
        String statusText = null;
        if (exception instanceof InvocationTargetException) {
            InvocationTargetException ite = (InvocationTargetException)exception;
            if (ite.getTargetException() != null) {
                statusText = ite.getTargetException().getMessage();
            }
        }
        if (StringUtils.isEmpty(statusText)) {
            statusText = exception.getMessage();
        }
        return statusText;
    }
}
