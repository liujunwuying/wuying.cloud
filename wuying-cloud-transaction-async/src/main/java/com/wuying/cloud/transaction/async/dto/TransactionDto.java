package com.wuying.cloud.transaction.async.dto;

import com.wuying.cloud.transaction.async.domain.Participant;
import com.wuying.cloud.transaction.async.enums.TransactionLevel;
import com.wuying.cloud.transaction.async.enums.TransactionStatus;
import lombok.Builder;
import lombok.Data;

/**
 * 事务类dto
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
@Data
@Builder
public class TransactionDto {

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
    private Integer retryInterval;

    /**
     * 最大重试次数
     */
    private Integer maxRetryTimes;

    /**
     * 事务级别
     */
    private Integer transactionLevel;

    /**
     * bean名称
     */
    private String bean;

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数
     */
    private String param;

    /**
     * 参数类型
     */
    private String paramTypes;

    /**
     * 已重试次数
     */
    private Integer retriedTimes = 0;

    /**
     * 事务状态
     */
    private Integer status;

    /**
     * 状态说明
     */
    private String statusText;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 上次更新时间
     */
    private Long lastUpdateTime;
}
