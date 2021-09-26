package com.wuying.cloud.transaction.async.service;

import com.wuying.cloud.transaction.async.domain.Transaction;

import java.util.List;

/**
 * 事务表操作接口类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-13
 */
public interface TransactionAsyncService {

    /**
     * 提交事务
     * @param txid 事务ID
     */
    void commitByTxid(String txid);

    /**
     * 提交事务
     * @param gxid 全局ID
     */
    void commitByGxid(String gxid);

    /**
     * 创建事务
     * @param transaction 事务对象
     */
    void create(Transaction transaction);

    /**
     * 更新事务状态
     * @param transactionList 事务列表
     */
    void updateStatus(List<Transaction> transactionList);

    /**
     * 查询需要重试的事务
     * @return 事务列表
     */
    List<Transaction> findRetryTransaction();

    /**
     * 清理历史数据
     * @param days 存活天数
     * @return 清理的数据量
     */
    int cleanTransactionByMaxDays(int days);

    /**
     * 获取事务状态
     * @param gxid 全局ID
     * @return 事务状态
     */
    int getStatusByGxid(String gxid);
}
