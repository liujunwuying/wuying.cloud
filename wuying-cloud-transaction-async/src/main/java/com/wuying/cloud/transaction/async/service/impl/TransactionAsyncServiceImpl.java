package com.wuying.cloud.transaction.async.service.impl;

import com.wuying.cloud.transaction.async.TransactionAsyncManager;
import com.wuying.cloud.transaction.async.dao.TransactionAsyncDao;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.enums.TransactionStatus;
import com.wuying.cloud.transaction.async.service.TransactionAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 事务表操作实现类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-22
 */
@Service
public class TransactionAsyncServiceImpl implements TransactionAsyncService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransactionAsyncDao transactionAsyncDao;

    @Autowired
    private TransactionAsyncManager transactionAsyncManager;

    /**
     * 提交事务
     * @param txid 事务ID
     */
    @Override
    public void commitByTxid(String txid) {
        Transaction transaction = transactionAsyncDao.getByTxid(txid);
        Assert.notNull(transaction, "事务数据不存在");
        try {
            transactionAsyncManager.commit(transaction);
        } catch (InterruptedException interruptedException) {
            logger.error("提交事务失败", interruptedException);
        }
    }

    /**
     * 提交事务
     * @param gxid 全局ID
     */
    @Override
    public void commitByGxid(String gxid) {
        List<Transaction> transactionList = transactionAsyncDao.getByGxid(gxid);
        Assert.notNull(transactionList, "事务数据不存在");
        for(Transaction transaction : transactionList) {
            try {
                transactionAsyncManager.commit(transaction);
            } catch (InterruptedException interruptedException) {
                logger.error("提交事务失败", interruptedException);
            }
        }
    }

    /**
     * 创建事务
     * @param transaction 事务对象
     */
    @Override
    public void create(Transaction transaction) {
        transactionAsyncDao.create(transaction);
    }

    /**
     * 更新事务状态
     * @param transactionList 事务列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(List<Transaction> transactionList) {
        transactionAsyncDao.updateStatus(transactionList);
    }

    /**
     * 查询需要重试的事务
     * @return 事务列表
     */
    @Override
    public List<Transaction> findRetryTransaction() {
        return transactionAsyncDao.findRetryTransaction();
    }

    /**
     * 清理历史数据
     * @param days 存活天数
     * @return 清理的数据量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanTransactionByMaxDays(int days) {
        return transactionAsyncDao.cleanTransactionByMaxDays(days);
    }

    /**
     * 获取事务状态
     * @param gxid 全局ID
     * @return 事务状态
     */
    @Override
    public int getStatusByGxid(String gxid) {
        List<Transaction> transactionList = transactionAsyncDao.getByGxid(gxid);
        Assert.notNull(transactionList, "事务数据不存在");
        if (TransactionStatus.success == transactionList.get(0).getParticipant().getStatus()) {
            for(Transaction transaction : transactionList) {
                if (TransactionStatus.success != transaction.getParticipant().getStatus()) {
                    return TransactionStatus.retry.getValue();
                }
            }
        }
        return transactionList.get(0).getParticipant().getStatus().getValue();
    }
}
