package org.ytor.sql4j.interceptor.support;

import org.ytor.sql4j.core.ExecResult;
import org.ytor.sql4j.interceptor.SqlInterceptor;
import org.ytor.sql4j.sql.SqlInfo;

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
