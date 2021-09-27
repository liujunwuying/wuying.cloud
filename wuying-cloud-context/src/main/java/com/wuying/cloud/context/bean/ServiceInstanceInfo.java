package com.wuying.cloud.context.bean;

import lombok.Builder;
import lombok.Data;

/**
 * 实例基本信息
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-27
 */
@Data
@Builder
public class ServiceInstanceInfo {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 端口号
     */
    private String serverPort;

    /**
     * 实例IP
     */
    private String ipAddress;

    /**
     * 实例主机名
     */
    private String hostname;

    /**
     * 进程号
     */
    private String processId;

    /**
     * 服务版本
     */
    private String appVersion;
}
