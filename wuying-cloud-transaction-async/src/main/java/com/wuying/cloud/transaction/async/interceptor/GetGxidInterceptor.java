package com.wuying.cloud.transaction.async.interceptor;

import com.wuying.cloud.transaction.async.constants.AsyncConstant;
import com.wuying.cloud.transaction.async.holder.ThreadLocalHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 获取gxid拦截器
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
public class GetGxidInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String gxid = request.getHeader(AsyncConstant.HEADER_GXID);
        if(StringUtils.hasText(gxid)) {
            ThreadLocalHolder.getGxid().set(gxid);
        }
        return true;
    }
}
