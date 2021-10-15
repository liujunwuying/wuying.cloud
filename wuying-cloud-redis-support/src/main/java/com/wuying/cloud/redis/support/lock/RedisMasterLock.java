package com.wuying.cloud.redis.support.lock;

import com.wuying.commons.logger.WuyingLogger;
import com.wuying.commons.master.MasterLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis实现主节点锁
 * 默认关闭，开启需配置(wuying.cloud.redis.support.lock.masterLockEnable=true)
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-29
 */
@Primary
@Component
public class RedisMasterLock implements MasterLock {

    private volatile boolean master = false;

    @Value("${spring.application.name:unknown}-masterLock")
    private String lockName;
    /**
     * 锁定时间，太小浪费服务器资源，且任务未完成已切换，太大则主从切换周期太长
     */
    @Value("${wuying.cloud.redis.support.lock.masterLockTime:60}")
    private int lockTime;

    @Value("${wuying.cloud.redis.support.lock.masterLockEnable:false}")
    private boolean masterLockEnable;

    @Autowired
    private RedisDistributedLock redisDistributedLock;

    @PostConstruct
    public void init() {
        Assert.isTrue(!"unknown-masterLock".equals(lockName), "spring.application.name can not be null");
        if (masterLockEnable) {
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> tryLock(), 1, lockTime, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean isMaster() {
        return master;
    }

    private void tryLock() {
        String requestId = UUID.randomUUID().toString();
        if (redisDistributedLock.tryLock(lockName, requestId, lockTime * 1000L, 30)) {
            master = true;
            WuyingLogger.info("master node in the next {} seconds, lockName : {}", lockTime, lockName);
        } else {
            master = false;
        }
    }
}
