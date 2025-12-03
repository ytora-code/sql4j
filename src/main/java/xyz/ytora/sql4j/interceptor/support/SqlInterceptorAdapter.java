package xyz.ytora.sql4j.interceptor.support;

import xyz.ytora.sql4j.core.ExecResult;
import xyz.ytora.sql4j.interceptor.SqlInterceptor;
import xyz.ytora.sql4j.sql.SqlInfo;

/**
 * 拦截器适配器
 */
public class SqlInterceptorAdapter implements SqlInterceptor {

    @Override
    public Boolean before(SqlInfo sqlInfo) {
        return true;
    }

    @Override
    public ExecResult after(SqlInfo sqlInfo, ExecResult result) {
        return result;
    }
}
