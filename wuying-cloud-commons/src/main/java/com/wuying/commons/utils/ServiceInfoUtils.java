package com.wuying.commons.utils;

import com.wuying.commons.logger.WuyingLogger;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 获取本机ip/hostname/processId
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-27
 */
public class ServiceInfoUtils {

    private static String ip;

    private static String hostname;

    private static String processId;

    static {
        try {
            String[] mxBeanInfos = ManagementFactory.getRuntimeMXBean().getName().split("@");
            processId = mxBeanInfos[0];
            hostname = mxBeanInfos[1];
            ip = initIp();
        } catch (Exception e) {
            WuyingLogger.error("获取ip/hostname/processId信息失败", e);
        }
    }

    private static String initIp() throws SocketException, UnknownHostException {
        String defaultIp = null;
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            // 在所有的接口下再遍历IP
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                // 排除loopback类型地址
                if (!inetAddress.isLoopbackAddress()) {
                    if (inetAddress.isSiteLocalAddress()) {
                        // site-local地址
                        return inetAddress.getHostAddress();
                    } else if (StringUtils.isEmpty(defaultIp)) {
                        // site-local类型的地址未被发现，先记录候选地址
                        defaultIp = inetAddress.getHostAddress();
                    }
                }
            }
        }
        if (StringUtils.isEmpty(defaultIp)) {
            defaultIp = InetAddress.getLocalHost().getHostAddress();
        }
        return defaultIp;
    }

    public static String getIp() {
        return ip;
    }

    public static String getHostname() {
        return hostname;
    }

    public static String getProcessId() {
        return processId;
    }
}
