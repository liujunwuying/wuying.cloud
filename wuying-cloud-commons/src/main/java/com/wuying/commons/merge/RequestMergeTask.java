package com.wuying.commons.merge;

import com.wuying.commons.logger.WuyingLogger;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * 请求合并任务类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-30
 */
public class RequestMergeTask<T> implements Runnable {

    /**
     * 当前任务所属线程名称
     */
    private String threadName;

    /**
     * 最大合并条数
     */
    private final int maxMergeSize;

    /**
     * 最大等待时间
     */
    private final int maxWaitTimeMs;

    /**
     * 上次执行时间
     */
    private volatile long lastExecuteTime;

    /**
     * 当前执行线程
     */
    private volatile Thread currentThread;

    /**
     * 缓存队列
     */
    private final BlockingQueue<T> blockingQueue;

    /**
     * 处理接口
     */
    private final RequestProcessor<T> requestProcessor;

    public RequestMergeTask(String threadName, int maxMergeSize, int maxWaitTimeMs,
                            int queueSize, RequestProcessor<T> requestProcessor) {
        this.threadName = threadName;
        this.maxMergeSize = maxMergeSize;
        this.maxWaitTimeMs = maxWaitTimeMs;
        this.lastExecuteTime = System.currentTimeMillis();
        this.requestProcessor = requestProcessor;
        this.blockingQueue = new ArrayBlockingQueue<>(queueSize);
    }

    @Override
    public void run() {
        currentThread = Thread.currentThread();
        if (StringUtils.hasText(threadName)) {
            currentThread.setName(threadName);
        }
        while (!currentThread.isInterrupted()) {
            while (!canExecute()) {
                LockSupport.park(this);
            }
            process();
        }
    }

    /**
     * 判断执行条件
     * @return boolean
     */
    private boolean canExecute() {
        return blockingQueue.size() >= maxMergeSize
                || System.currentTimeMillis() - lastExecuteTime >= maxWaitTimeMs;
    }

    /**
     * 调用业务方法的批量处理接口
     */
    private void process() {
        lastExecuteTime = System.currentTimeMillis();
        List<T> processList = new ArrayList<>(maxMergeSize);
        int drainSize = blockingQueue.drainTo(processList, maxMergeSize);
        if (drainSize > 0) {
            try {
                requestProcessor.process(processList);
            } catch (Exception e) {
                WuyingLogger.error("process error", e);
            }
        }
    }

    /**
     * 添加请求，达到最大条数则执行
     * @param request 请求
     * @return boolean
     */
    public boolean add(T request) {
        boolean result = blockingQueue.add(request);
        activateByBatchSize();
        return result;
    }

    /**
     * 超时执行
     */
    public void activateByTimeOut() {
        if (System.currentTimeMillis() - lastExecuteTime >= maxWaitTimeMs) {
            LockSupport.unpark(currentThread);
        }
    }

    /**
     * 达到最大条数执行
     */
    private void activateByBatchSize() {
        if (blockingQueue.size() >= maxMergeSize) {
            LockSupport.unpark(currentThread);
        }
    }


}
