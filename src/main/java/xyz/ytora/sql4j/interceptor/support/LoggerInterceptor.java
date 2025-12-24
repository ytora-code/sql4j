package xyz.ytora.sql4j.interceptor.support;

import xyz.ytora.sql4j.core.ExecResult;
import xyz.ytora.sql4j.enums.SqlType;
import xyz.ytora.sql4j.interceptor.SqlInterceptorAdapter;
import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 防止全表更新和删除
 */
public class LoggerInterceptor extends SqlInterceptorAdapter {

    @Override
    public Integer order() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public Boolean before(SqlInfo sqlInfo) {
        if (sqlInfo.getSqlType().equals(SqlType.DDL)) {
            sqlInfo.getSqlBuilder().getSQLHelper().getLogger().info("==== 即将执行DDL: " + sqlInfo.getSql());
        } else {
            // 记录 SQL
            sqlInfo.getSqlBuilder().getSQLHelper().getLogger().info("  SQL:\t" + sqlInfo.getSql());

            // 记录参数
            String orderedParmStr = "[ " +
                    sqlInfo.getOrderedParms().stream().map(i -> {
                        if (i == null) {
                            return "NULL( NULL )";
                        } else {
                            return i + "(" + i.getClass().getSimpleName() + ")";
                        }
                    }).collect(Collectors.joining(", ")) +
                    " ]";
            sqlInfo.getSqlBuilder().getSQLHelper().getLogger().info("PARMS:\t" + orderedParmStr);
        }
        return true;
    }

    @Override
    public ExecResult after(SqlInfo sqlInfo, ExecResult result) {
        if (sqlInfo.getSqlType().equals(SqlType.DDL)) {
            sqlInfo.getSqlBuilder().getSQLHelper().getLogger().info(" ==== DDL执行成功");
        }
        else if (sqlInfo.getSqlType().equals(SqlType.SELECT)) {
            // 记录 SQL 执行结果
            for (Map<String, Object> row : result.getResultList()) {
                sqlInfo.getSqlBuilder().getSQLHelper().getLogger().debug("<===\t" + row);
            }
        } else if (sqlInfo.getSqlType().equals(SqlType.INSERT)) {
            // 记录 SQL 执行结果
            sqlInfo.getSqlBuilder().getSQLHelper().getLogger().debug("<===\t 新增行数：" + result.getEffectedRows());
        } else {
            // 记录 SQL 执行结果
            sqlInfo.getSqlBuilder().getSQLHelper().getLogger().debug("<===\t 影响行数" + result.getEffectedRows());
        }
        return result;
    }

    @Override
    public void fail(SqlInfo sqlInfo, Exception e) {
        if (sqlInfo.getSqlType().equals(SqlType.DDL)) {
            sqlInfo.getSqlBuilder().getSQLHelper().getLogger().info("==== DDL执行失败: " + e.getMessage());
        } else {
            sqlInfo.getSqlBuilder().getSQLHelper().getLogger().error(e.getMessage());
        }
    }
}
