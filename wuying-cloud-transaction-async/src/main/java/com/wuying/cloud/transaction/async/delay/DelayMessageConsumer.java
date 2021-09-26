package com.wuying.cloud.transaction.async.delay;

import com.wuying.cloud.transaction.async.TransactionAsyncManager;
import com.wuying.cloud.transaction.async.delay.DelayQueueHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 延时消息消费者
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
@Component
public class DelayMessageConsumer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransactionAsyncManager transactionAsyncManager;

    @PostConstruct
    public void start() {
        (new Thread(() -> {
            while(true) {
                DelayMessage message = null;
                try {
                    message = DelayQueueHolder.get().take();
                    transactionAsyncManager.commit(message.getTransaction());
                } catch (InterruptedException ie) {
                    this.logger.warn("队列take被打断!", ie);
                }

            }
        }, this.getClass().getSimpleName())).start();
    }

}
