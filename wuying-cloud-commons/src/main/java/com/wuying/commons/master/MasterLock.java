package com.wuying.commons.master;

/**
 * 主节点锁
 * @author wuying
 * @since 1.0.0
 * @date 2021-10-08
 */
public interface MasterLock {

    /**
     * 判断当前节点是否是主节点
     * @return
     */
    boolean isMaster();
}
