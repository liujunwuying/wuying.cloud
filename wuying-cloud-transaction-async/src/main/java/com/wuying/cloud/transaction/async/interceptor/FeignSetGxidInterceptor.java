package com.wuying.cloud.transaction.async.interceptor;

import com.wuying.cloud.transaction.async.constants.AsyncConstant;
import com.wuying.cloud.transaction.async.holder.ThreadLocalHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Configuration;

/**
 * feign调用添加gxid
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-15
 */
@Configuration
public class FeignSetGxidInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String gxid = ThreadLocalHolder.getGxid().get();
        if (StringUtils.isNotBlank(gxid)) {
            template.header(AsyncConstant.HEADER_GXID, gxid);
        }
    }
}
