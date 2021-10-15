package com.wuying.cloud.context.holder;

import com.wuying.cloud.context.bean.ServiceInstanceInfo;
import com.wuying.commons.logger.WuyingLogger;
import com.wuying.commons.utils.ServiceInfoUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * 上下文持有类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-26
 */
public class ApplicationContextHolder implements ApplicationContextAware, EnvironmentAware {

    /**
     * 应用上下文
     */
    private static ApplicationContext applicationContext;

    /**
     * 环境上下文
     */
    private static Environment environment;

    /**
     * 实例基本信息
     */
    private static ServiceInstanceInfo serviceInstanceInfo;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        ApplicationContextHolder.environment = environment;
        String hostName = environment.getProperty("server.hostname");
        String ipAddress = environment.getProperty("server.ipAddress");
        serviceInstanceInfo = ServiceInstanceInfo.builder()
                .serviceName(environment.getProperty("spring.application.name"))
                .serverPort(environment.getProperty("server.port"))
                .hostname(StringUtils.isEmpty(hostName) ? ServiceInfoUtils.getHostname() : hostName)
                .ipAddress(StringUtils.isEmpty(ipAddress) ? ServiceInfoUtils.getIp() : ipAddress)
                .processId(ServiceInfoUtils.getProcessId())
                .appVersion(environment.getProperty("wuying.app.version", "-1")).build();
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static ServiceInstanceInfo getServiceInstanceInfo() {
        return serviceInstanceInfo;
    }

    /**
     * getBean by type
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * getBean by name
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * getBean by name and type
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * getProperty by key
     */
    public static String getProperty(String key) {
        return environment.getProperty(key);
    }

    /**
     * getProperty with defaultValue
     */
    public static String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }
}
