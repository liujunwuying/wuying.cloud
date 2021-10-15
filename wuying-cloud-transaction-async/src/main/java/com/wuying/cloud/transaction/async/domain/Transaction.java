package com.wuying.cloud.transaction.async.domain;

import lombok.Builder;
import lombok.Data;

/**
 * 事务类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
@Data
@Builder
public class Transaction {

    /**
     * 事务ID
     */
    private String txid;

    /**
     * 全局事务ID
     */
    private String gxid;

    /**
     * 事务所在服务名
     */
    private String applicationName;

    /**
     * 事务发起者
     */
    private String coordinator;

    /**
     * 重试间隔时间
     */
    private int retryInterval;

    /**
     * 最大重试次数
     */
    private int maxRetryTimes;

    /**
     * 参与者
     */
    private Participant participant;
}
