package com.wuying.cloud.transaction.async;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey.Factory;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.wuying.cloud.context.holder.ApplicationContextHolder;
import com.wuying.cloud.transaction.async.domain.Participant;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.holder.ThreadLocalHolder;

import java.lang.reflect.Method;

/**
 * invoker类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
public class TransactionAsyncInvoker  extends HystrixCommand<Object> {

    private final Transaction transaction;

    public TransactionAsyncInvoker(Transaction transaction) {
        super(Setter.withGroupKey(
                Factory.asKey("TransactionAsyncInvoker"))
                .andCommandKey(HystrixCommandKey.Factory.asKey(transaction.getParticipant().getBeanName()
                        + "." + transaction.getParticipant().getMethod().getName()))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("AsyncInvokerThreadPool_"
                        + transaction.getApplicationName())));
        this.transaction = transaction;
    }

    @Override
    protected Object run() throws Exception {
        ThreadLocalHolder.getGxid().set(this.transaction.getGxid());
        Participant participant = transaction.getParticipant();
        Object result = participant.getMethod().invoke(
                ApplicationContextHolder.getBean(participant.getBeanName()),
                participant.getParam());
        ThreadLocalHolder.getGxid().remove();
        return result;
    }

}
