package com.wuying.commons.merge;

import org.springframework.util.Assert;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 请求合并执行器
 * @author wuying
 * @since 1.0.0
 * @date 2021-10-08
 */
public class RequestMergeExecutor<T> {

    private static final int DEFAULT_QUEUE_SIZE = 10000;

    private static final int DEFAULT_MAX_MERGE_SIZE = 20;

    private static final int DEFAULT_MAX_WAIT_TIME = 1000;

    private static final int DEFAULT_THREAD_NUM = 1;

    private static final int MAX_MERGE_THREAD = 64;

    private final RequestMergeTask<T>[] mergeTasks;

    private static final Random RANDOM = new Random();

    /**
     * 超时执行线程池
     */
    private static ScheduledExecutorService timeoutExecutor = new ScheduledThreadPoolExecutor(DEFAULT_THREAD_NUM);

    /**
     * 合并请求线程池
     */
    private static ExecutorService executorThreadPool = new ThreadPoolExecutor(DEFAULT_THREAD_NUM, MAX_MERGE_THREAD,
            60L, TimeUnit.SECONDS, new SynchronousQueue<>());

    /**
     *
     * @param requestProcessor 合并请求处理接口
     */
    public RequestMergeExecutor(RequestProcessor<T> requestProcessor) {
        Assert.notNull(requestProcessor, "requestProcessor can not be null");
        this.mergeTasks = new RequestMergeTask[DEFAULT_THREAD_NUM];
        RequestMergeTask<T> executeTask = new RequestMergeTask<>(null, DEFAULT_MAX_MERGE_SIZE,
                DEFAULT_MAX_WAIT_TIME, DEFAULT_QUEUE_SIZE, requestProcessor);
        mergeTasks[0] = executeTask;
        executorThreadPool.submit(executeTask);
        timeoutExecutor.scheduleAtFixedRate(executeTask::activateByTimeOut, RANDOM.nextInt(DEFAULT_MAX_WAIT_TIME),
                DEFAULT_MAX_WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    public RequestMergeExecutor(String threadName, int maxMergeSize, int maxWaitTimeMs, int threadNum,
                                int queueSize, RequestProcessor<T> requestProcessor) {
        Assert.isTrue(threadNum >= 1 && threadNum <= MAX_MERGE_THREAD, "threadNum取值范围1-64");
        Assert.isTrue(maxMergeSize > 0 && maxWaitTimeMs > 0, "maxMergeSize与maxWaitTimeMs必须大于0");
        Assert.isTrue(queueSize >= maxMergeSize, "queueSize需要大于等于maxMergeSize,建议queueSize至少为maxMergeSize的10倍");
        Assert.notNull(requestProcessor, "requestProcessor can not be null");

        this.mergeTasks = new RequestMergeTask[threadNum];
        for (int i = 0; i < threadNum; i++) {
            RequestMergeTask<T> executeTask = new RequestMergeTask<>(threadName + "-" + i, maxMergeSize,
                    maxWaitTimeMs, queueSize, requestProcessor);
            mergeTasks[i] = executeTask;
            executorThreadPool.submit(executeTask);
            timeoutExecutor.scheduleAtFixedRate(executeTask::activateByTimeOut, RANDOM.nextInt(maxWaitTimeMs),
                    maxWaitTimeMs, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 添加请求
     * @param request 请求对象
     * @return boolean
     */
    public boolean add(T request) {
        if (mergeTasks.length == 1) {
            return mergeTasks[0].add(request);
        }
        return mergeTasks[RANDOM.nextInt(mergeTasks.length)].add(request);
    }
}
