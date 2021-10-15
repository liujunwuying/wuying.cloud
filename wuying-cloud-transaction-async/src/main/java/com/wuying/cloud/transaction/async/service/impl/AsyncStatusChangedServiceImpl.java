package com.wuying.cloud.transaction.async.service.impl;

import com.wuying.cloud.transaction.async.constants.AsyncConstant;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.service.AsyncStatusChangedService;
import com.wuying.commons.merge.RequestMergeExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * 事务状态变更impl
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public class AsyncStatusChangedServiceImpl implements AsyncStatusChangedService {

    @Value("${wuying.cloud.transaction.async.statusUpdateThreadNum:1}")
    private int statusUpdateThreadNum;

    private RequestMergeExecutor<Transaction> updateStatusExecutor;

    @Autowired
    private UpdateAsyncStatusProcessor updateAsyncStatusProcessor;

    @PostConstruct
    public void init() {
        this.updateStatusExecutor = new RequestMergeExecutor<>("UpdateStatusThread",
                AsyncConstant.UPDATE_STATUS_BATCH_SIZE, AsyncConstant.UPDATE_STATUS_DELAY_TIME,
                statusUpdateThreadNum, 100000, updateAsyncStatusProcessor);
    }
    @Override
    public void notifyChanged(Transaction transaction){
        this.updateStatusExecutor.add(transaction);
    }
}
