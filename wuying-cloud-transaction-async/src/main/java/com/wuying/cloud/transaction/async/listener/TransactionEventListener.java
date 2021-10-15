package com.wuying.cloud.transaction.async.listener;

import com.wuying.cloud.context.threadpool.NamedThreadFactory;
import com.wuying.cloud.transaction.async.TransactionAsyncManager;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.properties.ThreadPoolProperties;
import com.wuying.commons.logger.WuyingLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事务处理监听器
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
@Component
public class TransactionEventListener {

    @Autowired
    private TransactionAsyncManager transactionAsyncManager;

    @Autowired
    private ThreadPoolProperties threadPoolProperties;

    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void init() {
        threadPoolExecutor = new ThreadPoolExecutor(
                threadPoolProperties.getCorePoolSize(),
                threadPoolProperties.getMaximumPoolSize(),
                threadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(threadPoolProperties.getQueueSize()),
                new NamedThreadFactory("transaction-async-commit"));
    }

    @TransactionalEventListener
    public void handle(PayloadApplicationEvent<Transaction> event) {
        final Transaction transaction = event.getPayload();

        threadPoolExecutor.execute(()->{
            try {
                transactionAsyncManager.commit(transaction);
            } catch (Exception e) {
                WuyingLogger.error("TransactionEventListener.handle()", e);
            }
        });
    }

    @PreDestroy
    public void destroy() {
        threadPoolExecutor.shutdown();
    }
}
