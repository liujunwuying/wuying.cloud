package com.wuying.cloud.transaction.async.delay;

import java.util.concurrent.DelayQueue;

/**
 * 延时队列holder
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
public class DelayQueueHolder {

    private static final DelayQueue<DelayMessage> delayQueue = new DelayQueue();

    public static DelayQueue<DelayMessage> get() {
        return delayQueue;
    }
}
