package com.wuying.cloud.redis.support.lock;

import com.wuying.commons.logger.WuyingLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁实现，不支持哨兵模式/集群模式
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-29
 */
@Component
public class RedisDistributedLock {

    /**
     * key前缀
     */
    private static final String PREFIX = "lock_";

    /**
     * unlock成功标识
     */
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * unlock脚本
     */
    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取锁，带最大等待时间
     * @param lockName 锁key，全局唯一，建议至少分两段(服务名_业务名)
     * @param requestId 请求ID，用于解锁，可用UUID生成
     * @param expiredInMilliSeconds 过期毫秒数，设置比业务需要时间长，结束时调用unLock
     * @param maxWaitTimeInMilliSeconds 最大等待时间
     * @return
     */
    public Boolean tryLock(String lockName, String requestId, long expiredInMilliSeconds, long maxWaitTimeInMilliSeconds) {
        final long startTime = System.currentTimeMillis();
        while (!tryLock(lockName, requestId, expiredInMilliSeconds)) {
            if (System.currentTimeMillis() - startTime > maxWaitTimeInMilliSeconds) {
                return Boolean.FALSE;
            }
            try {
                Thread.sleep(10L);
            } catch (InterruptedException interruptedException) {
                WuyingLogger.warn("thread interruptedException when try lock", interruptedException);
            }
        }
        return Boolean.TRUE;

    }

    /**
     * 获取锁
     * @param lockName 锁key，全局唯一，建议至少分两段(服务名_业务名)
     * @param requestId 请求ID，用于解锁，可用UUID生成
     * @param expiredInMilliSeconds 过期毫秒数，设置比业务需要时间长，结束时调用unLock
     * @return 成功失败状态
     */
    public Boolean tryLock(String lockName, String requestId, long expiredInMilliSeconds) {
        try {
            return stringRedisTemplate.opsForValue().setIfAbsent(PREFIX + lockName, requestId, expiredInMilliSeconds, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            WuyingLogger.error("get redis distributed lock fail, lockName:" + lockName, e);

        }
        return Boolean.FALSE;
    }

    /**
     * 释放锁
     * @param lockName 锁key
     * @param requestId 请求ID
     * @return 成功失败状态
     */
    public Boolean unlock(String lockName, String requestId) {
        RedisScript<Long> releaseScript = new DefaultRedisScript<>(RELEASE_LOCK_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(releaseScript, Collections.singletonList(PREFIX + lockName), requestId);
        return RELEASE_SUCCESS.equals(result);
    }
}
