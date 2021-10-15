package com.wuying.cloud.transaction.async.domain;

import com.wuying.cloud.transaction.async.enums.TransactionStatus;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 参与者类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
@Data
@Builder
public class Participant {

    /**
     * bean名称
     */
    private String beanName;

    /**
     * 方法
     */
    private Method method;

    /**
     * 参数
     */
    private Object[] param;

    /**
     * 参数类型
     */
    private Class<?>[] paramTypes;

    /**
     * 已重试次数
     */
    private int retriedTimes = 0;

    /**
     * 事务状态
     */
    private TransactionStatus status;

    /**
     * 状态说明
     */
    private String statusText;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 上次更新时间
     */
    private long lastUpdateTime;
}
