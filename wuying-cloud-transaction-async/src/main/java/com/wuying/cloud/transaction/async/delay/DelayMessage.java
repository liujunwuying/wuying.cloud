package com.wuying.cloud.transaction.async.delay;

import com.wuying.cloud.transaction.async.domain.Transaction;
import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时消息
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
@Data
public class DelayMessage implements Delayed {

    /**
     * 事务对象
     */
    private Transaction transaction;

    /**
     * 执行时间（ms）
     */
    private long executeTime;

    public DelayMessage(Transaction transaction, long delayTime) {
        this.transaction = transaction;
        this.executeTime = System.currentTimeMillis() + delayTime;
    }

    @Override
    public int compareTo(Delayed object) {
        return 1;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.executeTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
}
