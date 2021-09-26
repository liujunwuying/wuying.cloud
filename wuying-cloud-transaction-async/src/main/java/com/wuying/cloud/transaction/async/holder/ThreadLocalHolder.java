package com.wuying.cloud.transaction.async.holder;

/**
 * 保存gxid
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
public class ThreadLocalHolder {
    private static final ThreadLocal<String> gxid = new InheritableThreadLocal();

    public static ThreadLocal<String> getGxid() {
        return gxid;
    }
}
