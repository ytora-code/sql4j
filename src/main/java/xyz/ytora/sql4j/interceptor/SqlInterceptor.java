package xyz.ytora.sql4j.interceptor;

import xyz.ytora.sql4j.core.ExecResult;
import xyz.ytora.sql4j.sql.SqlInfo;

/**
 * 拦截器
 */
public interface SqlInterceptor {
    /**
     * SQL 提交数据库之前进行拦截
     * @param sqlInfo 即将执行的 SQL 信息
     * @return 如果为ture则提交，如果为false则拦截
     */
    Boolean before(SqlInfo sqlInfo);

    /**
     * SQL 提交数据库之后
     * @param sqlInfo 执行的 SQL 信息
     * @param result 上一个拦截器的结果
     * @return 真正返给客户端的执行结果
     */
    ExecResult after(SqlInfo sqlInfo, ExecResult result);
}
