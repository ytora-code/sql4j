package xyz.ytora.sql4j.interceptor;

import xyz.ytora.sql4j.core.ExecResult;
import xyz.ytora.sql4j.sql.SqlBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;

/**
 * 拦截器适配器
 */
public class SqlInterceptorAdapter implements SqlInterceptor {

    @Override
    public Integer order() {
        return 0;
    }

    @Override
    public SqlBuilder beforeTranslate(SqlBuilder sqlBuilder) {
        return sqlBuilder;
    }

    @Override
    public Boolean before(SqlInfo sqlInfo) {
        return true;
    }

    @Override
    public ExecResult after(SqlInfo sqlInfo, ExecResult result) {
        return result;
    }

    @Override
    public void fail(SqlInfo sqlInfo, Exception e) {

    }
}
