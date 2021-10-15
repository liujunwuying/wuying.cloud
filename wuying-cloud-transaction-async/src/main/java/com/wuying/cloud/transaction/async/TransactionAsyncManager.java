package com.wuying.cloud.transaction.async;

import com.wuying.cloud.transaction.async.delay.DelayMessage;
import com.wuying.cloud.transaction.async.delay.DelayQueueHolder;
import com.wuying.cloud.transaction.async.domain.Participant;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.enums.RetryIntervalLevel;
import com.wuying.cloud.transaction.async.enums.TransactionStatus;
import com.wuying.cloud.transaction.async.service.AsyncStatusChangedService;
import com.wuying.commons.logger.WuyingLogger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * 异步确保管理类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
@Component
public class TransactionAsyncManager {

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
            new TransactionAsyncInvoker(transaction).execute();
            participant.setRetriedTimes(participant.getRetriedTimes() + 1);
            participant.setStatus(TransactionStatus.success);
            participant.setStatusText(TransactionStatus.success.getName());
        } catch (Exception exception) {
            String statusText = getStatusText(exception);
            participant.setRetriedTimes(participant.getRetriedTimes() + 1);
            if (participant.getRetriedTimes() < transaction.getMaxRetryTimes()) {
                WuyingLogger.warn("transaction[{}]fail, waiting for retry:{} -> {}.{}",
                        transaction.getTxid(),
                        transaction.getCoordinator(),
                        participant.getBeanName(),
                        participant.getMethod().getName());
                participant.setStatus(TransactionStatus.retry);
                participant.setStatusText(statusText);
                /**
                 * 重试间隔
                 */
                long initRetryInterval = 10L;
                long delayTime = (long)(initRetryInterval * Math.pow(transaction.getParticipant().getRetriedTimes(), 2));
                if (delayTime > RetryIntervalLevel.quick.getInterval() * 60L ) {
                    delayTime = RetryIntervalLevel.quick.getInterval() * 60L;
                }
                boolean sizeExceed = false;
                if (counter % 100 == 0&& DelayQueueHolder.get().size() >= maxDelayQueueSize) {
                    sizeExceed = true;
                    counter = 0;
                }
                if(!sizeExceed && (participant.getRetriedTimes() <= RetryIntervalLevel.quick.getCount() )) {
                    DelayMessage message = new DelayMessage(transaction, delayTime);
                    DelayQueueHolder.get().offer(message);
                    ++counter;
                }
            } else {
                WuyingLogger.error("transaction {} fail finally : {} -> {}.{}",
                        transaction.getTxid(),
                        transaction.getCoordinator(),
                        participant.getBeanName(),
                        participant.getMethod().getName(),
                        exception);
                participant.setStatus(TransactionStatus.fail);
                participant.setStatusText(statusText);
            }
        } finally {
            transaction.getParticipant().setLastUpdateTime(System.currentTimeMillis());
            asyncStatusChangedService.notifyChanged(transaction);
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
