package com.wuying.cloud.transaction.async.proxy;

import com.wuying.cloud.context.holder.ApplicationContextHolder;
import com.wuying.cloud.transaction.async.domain.Participant;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.holder.ApplicationEventPublisherHolder;
import com.wuying.cloud.transaction.async.holder.ThreadLocalHolder;
import com.wuying.cloud.transaction.async.service.TransactionAsyncService;
import com.wuying.cloud.transaction.async.util.TxidGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import java.lang.reflect.Method;

/**
 * 事务代理类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public abstract class AbstractTransactionProxy {

    protected Transaction transaction;

    protected Object realSubject;

    protected Class<?> beanType;

    public AbstractTransactionProxy(Object realSubject, Class<?> beanType, Transaction transaction) {
        this.realSubject = realSubject;
        this.beanType = beanType;
        this.transaction = transaction;
    }

    /**
     * 生成代理对象
     * @return
     */
    public abstract Object generateProxy();

    protected Object intercept(Object obj, Method method, Object[] args) throws Throwable {
        long date = System.currentTimeMillis();
        Participant participant = Participant.builder()
                .beanName(beanType.getName())
                .method(method)
                .param(args)
                .paramTypes(method.getParameterTypes())
                .createTime(date)
                .lastUpdateTime(date)
                .build();

        Transaction entity = Transaction.builder().build();
        BeanUtils.copyProperties(transaction, entity);
        entity.setParticipant(participant);
        setTxidFromThreadLocal(entity);
        ApplicationContextHolder.getBean(TransactionAsyncService.class).create(entity);
        ApplicationEventPublisherHolder.getEventPublisher().publishEvent(entity);
        return null;
    }

    /**
     * 设置txid/gxid
     * @param transaction 事务对象
     */
    private void setTxidFromThreadLocal(Transaction transaction) {
        String gxid = ThreadLocalHolder.getGxid().get();
        if (StringUtils.isNotBlank(gxid)) {
            transaction.setGxid(gxid);
            transaction.setTxid(TxidGenerator.generate());
        } else {
            transaction.setGxid(TxidGenerator.generate());
            transaction.setTxid(transaction.getGxid());
            ThreadLocalHolder.getGxid().set(transaction.getGxid());
        }
    }
}
