package com.wuying.cloud.transaction.async.constants;

/**
 * 常量定义
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
public interface AsyncConstant {

    /**
     * gxid请求头
     */
    String HEADER_GXID = "WY_TRANSACTION_ASYNC_GXID";

    /**
     * 状态更新延时时间
     */
    int UPDATE_STATUS_DELAY_TIME = 2000;

    /**
     * 状态更新批处理数量
     */
    int UPDATE_STATUS_BATCH_SIZE = 50;

}
