package com.wuying.cloud.transaction.async.service;

import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.dto.TransactionDto;
import com.wuying.cloud.transaction.async.util.JsonUtil;
import org.springframework.util.Assert;

/**
 * 事务状态变更service
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public interface AsyncStatusChangedService {

    /**
     * 通知状态变更
     * @param transaction
     */
    void notifyChanged(Transaction transaction);

    /**
     * entityTODto
     * @param transaction
     * @return
     */
    default TransactionDto entityTODto(Transaction transaction) {
        Assert.notNull(transaction, "入参为空");
        return TransactionDto.builder()
                .applicationName(transaction.getApplicationName())
                .gxid(transaction.getGxid())
                .txid(transaction.getTxid())
                .maxRetryTimes(transaction.getMaxRetryTimes())
                .retryInterval(transaction.getRetryInterval())
                .coordinator(transaction.getCoordinator())
                .retriedTimes(transaction.getParticipant().getRetriedTimes())
                .bean(transaction.getParticipant().getBeanName())
                //.method(transaction.getParticipant().getMethod())
                .paramTypes(JsonUtil.writeValueAsString(transaction.getParticipant().getParamTypes()))
                .status(transaction.getParticipant().getStatus().getValue())
                .statusText(transaction.getParticipant().getStatusText())
                .createTime(transaction.getParticipant().getCreateTime())
                .lastUpdateTime(transaction.getParticipant().getLastUpdateTime())
                .build();
    }
}
