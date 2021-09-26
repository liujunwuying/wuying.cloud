package com.wuying.cloud.transaction.async.interceptor;

import com.wuying.cloud.transaction.async.constants.AsyncConstant;
import com.wuying.cloud.transaction.async.holder.ThreadLocalHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * restTemplate调用添加gxid
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
@Component
public class RestSetGxidInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String gxid = ThreadLocalHolder.getGxid().get();
        if (StringUtils.isNotBlank(gxid)) {
            request.getHeaders().add(AsyncConstant.HEADER_GXID, gxid);
        }
        return execution.execute(request, body);
    }
}
