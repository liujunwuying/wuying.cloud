package com.wuying.commons.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * logger工具类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-26
 */
public class WuyingLogger {

    /**
     * 缓存Logger对象，软引用内存不足时可回收
     */
    private static Map<String, SoftReference<Logger>> loggerMap = new ConcurrentHashMap<>();

    private static final Logger wuyingLogger = LoggerFactory.getLogger(WuyingLogger.class);

    private static Logger getCallerLogger() {
        try {
            /**
             * 上推两层
             * [0].methodName=getStackTrace
             * [1].methodName=getCallerLogger
             * [2].methodName=warn/info/error
             * [3]=调用方法
             */
            String className = Thread.currentThread().getStackTrace()[3].getClassName();
            if (loggerMap.containsKey(className)
                    && loggerMap.get(className) != null
                    && loggerMap.get(className).get() != null) {
                return loggerMap.get(className).get();
            } else {
                Logger logger = LoggerFactory.getLogger(Class.forName(className));
                loggerMap.put(className, new SoftReference<>(logger));
                return logger;
            }
        } catch (Exception exception) {
            wuyingLogger.error("创建日志对象失败，使用wuyingLogger兜底!", exception);
        }
        return wuyingLogger;
    }

    public static void info(String msg) {
        getCallerLogger().info(msg);
    }

    public static void info(String format, Object arg) {
        getCallerLogger().info(format, arg);
    }

    public static void info(String format, Object... arguments) {
        getCallerLogger().info(format, arguments);
    }

    public static void info(String msg, Throwable t){
        getCallerLogger().info(msg, t);
    }

    public static void warn(String msg) {
        getCallerLogger().warn(msg);
    }

    public static void warn(String format, Object arg) {
        getCallerLogger().warn(format, arg);
    }

    public static void warn(String format, Object... arguments) {
        getCallerLogger().warn(format, arguments);
    }

    public static void warn(String msg, Throwable t){
        getCallerLogger().warn(msg, t);
    }


    public static void debug(String msg) {
        getCallerLogger().debug(msg);
    }

    public static void debug(String format, Object arg) {
        getCallerLogger().debug(format, arg);
    }

    public static void debug(String format, Object... arguments) {
        getCallerLogger().debug(format, arguments);
    }

    public static void debug(String msg, Throwable t){
        getCallerLogger().debug(msg, t);
    }

    public static void trace(String msg) {
        getCallerLogger().trace(msg);
    }

    public static void trace(String format, Object arg) {
        getCallerLogger().trace(format, arg);
    }

    public static void trace(String format, Object... arguments) {
        getCallerLogger().trace(format, arguments);
    }

    public static void trace(String msg, Throwable t){
        getCallerLogger().trace(msg, t);
    }

    public static void error(String msg) {
        getCallerLogger().error(msg);
    }

    public static void error(String format, Object arg) {
        getCallerLogger().error(format, arg);
    }

    public static void error(String format, Object... arguments) {
        getCallerLogger().error(format, arguments);
    }

    public static void error(String msg, Throwable t){
        getCallerLogger().error(msg, t);
    }

    public static boolean isDebugEnabled() {
        return getCallerLogger().isDebugEnabled();
    }
}
