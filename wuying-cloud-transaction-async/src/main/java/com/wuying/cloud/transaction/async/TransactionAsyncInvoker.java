package com.wuying.cloud.transaction.async;

import com.netflix.hystrix.HystrixCommand;
import com.wuying.cloud.transaction.async.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * invoker类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
public class TransactionAsyncInvoker  {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ApplicationContext context;

    private String beanName;

    private Method method;

    private Object[] param;

    private Transaction transaction;

}
