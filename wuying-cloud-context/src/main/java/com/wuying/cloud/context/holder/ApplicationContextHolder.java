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
        if (applicationContext != null) {
            try {
                return applicationContext.getBean(requiredType);
            } catch (BeansException beansException) {
                WuyingLogger.error("getBean error, class:" + requiredType.getName(), beansException);
            }
        }
        return null;
    }

    /**
     * getBean by name
     */
    public Object getBean(String name) {
        if (applicationContext != null) {
            try {
                return applicationContext.getBean(name);
            } catch (BeansException beansException) {
                WuyingLogger.error("getBean error, name:" + name, beansException);
            }
        }
        return null;
    }

    /**
     * getBean by name and type
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        if (applicationContext != null) {
            try {
                return applicationContext.getBean(name, requiredType);
            } catch (BeansException beansException) {
                WuyingLogger.error("getBean error, name:" + name + ", class:" + requiredType.getName(), beansException);
            }
        }
        return null;
    }

    /**
     * getProperty by key
     */
    public static String getProperty(String key) {
        if (environment == null) {
            return null;
        }
        return environment.getProperty(key);
    }

    /**
     * getProperty with defaultValue
     */
    public static String getProperty(String key, String defaultValue) {
        if (environment == null) {
            return null;
        }
        return environment.getProperty(key, defaultValue);
    }
}
