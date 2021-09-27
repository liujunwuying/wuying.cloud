package com.wuying.cloud.transaction.async.util;

import com.wuying.commons.utils.ServiceInfoUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.management.ManagementFactory;

/**
 * 事务处理监听器
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-22
 */
public class TxidGenerator {

    private static int counter = 1;
    private static long lastTime;
    private static String hostname;
    private static String pid;

    static {
        hostname = StringUtils.leftPad(String.valueOf(Math.abs(ServiceInfoUtils.getHostname().hashCode())), 10, "0");
        pid = StringUtils.leftPad(ServiceInfoUtils.getProcessId(), 5, "0");
    }

    public static synchronized String generate() {
        StringBuffer buffer = new StringBuffer();
        long currentMillis = System.currentTimeMillis();
        if (lastTime == currentMillis) {
            int next = counter++;
            if (next > 9999) {
                for (next = 1; currentMillis == lastTime; currentMillis = System.currentTimeMillis()) {
                }
            }
            buffer.append(currentMillis);
            buffer.append(StringUtils.leftPad(String.valueOf(next), 4, "0"));
        } else {
            buffer.append(currentMillis);
            buffer.append("0001");
            counter = 2;
        }
        buffer.append(hostname);
        buffer.append(pid);
        lastTime = currentMillis;
        return buffer.toString();
    }
}
