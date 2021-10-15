package com.wuying.cloud.transaction.async.task;

import com.wuying.cloud.transaction.async.TransactionAsyncManager;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.service.TransactionAsyncService;
import com.wuying.commons.logger.WuyingLogger;
import com.wuying.commons.master.MasterLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 事务状态检查任务
 * @author wuying
 * @since 1.0.0
 * @date 2021-10-08
 */
@Component
public class TransactionCheckTask {

    @Autowired
    private TransactionAsyncService transactionAsyncService;

    @Autowired
    private TransactionAsyncManager transactionAsyncManager;

    @Autowired
    private MasterLock masterLock;

    @Scheduled(fixedDelay = 60000L)
    public void checkAndInvoke() {
        if (masterLock.isMaster()) {
            while (true) {
                try {
                    List<Transaction> transactionList = transactionAsyncService.findRetryTransaction();
                    if (CollectionUtils.isEmpty(transactionList)) {
                        return;
                    }
                    for(Transaction transaction : transactionList) {
                        transactionAsyncManager.commit(transaction);
                    }
                    Thread.sleep(3000L);
                } catch (Exception e) {
                    WuyingLogger.error("transaction chek task fail", e);
                }
            }
        }
    }
}
