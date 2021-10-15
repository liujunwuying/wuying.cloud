package com.wuying.cloud.transaction.async.task;

import com.wuying.cloud.transaction.async.service.TransactionAsyncService;
import com.wuying.commons.logger.WuyingLogger;
import com.wuying.commons.master.MasterLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 事务清理任务
 * @author wuying
 * @since 1.0.0
 * @date 2021-10-08
 */
@Component
public class TransactionCleanTask {

    @Value("{wuying.cloud.transaction.async.reserveDays:30}")
    private int reserveDays;

    @Autowired
    private TransactionAsyncService transactionAsyncService;

    @Autowired
    private MasterLock masterLock;

    @Scheduled(cron = "28 0 0 */1 * *")
    public void clean() {
        if (masterLock.isMaster()) {
            try {
                int count = transactionAsyncService.cleanTransactionByMaxDays(reserveDays);
                WuyingLogger.info("clean transaction log success, remove {} records", count);
            } catch (Exception e) {
                WuyingLogger.error("transaction clean task fail", e);
            }
        }
    }
}
