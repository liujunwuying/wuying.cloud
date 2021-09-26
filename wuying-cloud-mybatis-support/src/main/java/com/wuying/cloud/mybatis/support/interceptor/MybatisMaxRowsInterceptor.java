package com.wuying.cloud.mybatis.support.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/**
 * 最大查询条数拦截器
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-24
 */
@ConfigurationProperties(prefix = "wuying.cloud.mybatis.support")
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class MybatisMaxRowsInterceptor implements Interceptor {

    private Integer maxRow = 10000;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Statement statement = (Statement)invocation.proceed();
        if (maxRow > 0) {
            statement.setMaxRows(maxRow);
        }
        return statement;
    }

    @Override
    public Object plugin(Object target){
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties){}

    public Integer getMaxRow() {
        return maxRow;
    }

    public void setMaxRow(Integer maxRow) {
        this.maxRow = maxRow;
    }
}
