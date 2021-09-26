package com.wuying.cloud.transaction.async.listener;

import com.wuying.cloud.transaction.async.TransactionAsyncManager;
import com.wuying.cloud.transaction.async.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransactionAsyncManager transactionAsyncManager;

    @Value("{wuying.cloud.transaction.async.threadpool.coreSize:2}")
    private int coreThreadSize;

    @Value("{wuying.cloud.transaction.async.threadpool.maxSize:10}")
    private int maxThreadSize;

    @Value("{wuying.cloud.transaction.async.threadpool.queueSize:1000}")
    private int queueSize;

    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void init() {
        threadPoolExecutor = new ThreadPoolExecutor(coreThreadSize, maxThreadSize, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(queueSize));
    }

    @TransactionalEventListener
    public void handle(PayloadApplicationEvent<Transaction> event) {
        final Transaction transaction = event.getPayload();

        try {
            threadPoolExecutor.execute(()->{
                try {
                    transactionAsyncManager.commit(transaction);
                } catch (Exception e) {
                    logger.warn("TransactionEventListener.handle()", e);
                }
            });
        } catch (Exception e) {
            logger.warn("system busy, transaction[{}] abandon execute immediately, will execute a few mintes later", transaction.getTxid());
        }
    }

    @PreDestroy
    public void destroy() {
        threadPoolExecutor.shutdown();
    }
}
