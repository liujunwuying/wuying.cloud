package com.wuying.cloud.transaction.async.service.impl;

import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.service.TransactionAsyncService;
import com.wuying.commons.merge.RequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 事务状态变更合并处理器
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public class UpdateAsyncStatusProcessor implements RequestProcessor<Transaction> {

    @Autowired
    private TransactionAsyncService transactionAsyncService;

    @Override
    public void process(List<Transaction> list) {
        transactionAsyncService.updateStatus(list);
    }
}
