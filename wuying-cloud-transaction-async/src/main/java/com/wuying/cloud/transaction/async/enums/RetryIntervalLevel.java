package com.wuying.cloud.transaction.async.enums;


/**
 * 重试间隔
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
public enum RetryIntervalLevel {

    quick(4,2),
    slow(10,60);

    /**
     * 次数
     */
    private int count;

    /**
     * 间隔
     */
    private int interval;

    private RetryIntervalLevel(int count, int interval) {
        this.count = count;
        this.interval = interval;
    }

    public int getCount() {
        return count;
    }

    public int getInterval() {
        return interval;
    }
}
