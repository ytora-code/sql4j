package xyz.ytora.sql4j.sql;

import xyz.ytora.sql4j.enums.SqlType;
import xyz.ytora.sql4j.util.Sql4jUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * SQL 状态
 */
public class SqlInfo {

    /**
     * 拦截器是否启用
     */
    private Boolean interceptorEnabled = true;

    /**
     * SQL 的构建器对象
     */
    private final SqlBuilder sqlBuilder;

    /**
     * SQL 类型
     */
    private final SqlType sqlType;

    /**
     * SQL 语句
     */
    private final String sql;

    /**
     * SQL 语句的占位符
     */
    private final List<Object> orderedParms;

    /**
     * SQL 耗时
     */
    private Long costMillis;

    public SqlInfo(SqlBuilder sqlBuilder, SqlType sqlType, String sql, List<Object> orderedParms) {
        this.sqlBuilder = sqlBuilder;
        this.sqlType = sqlType;
        this.sql = sql.trim();
        this.orderedParms = orderedParms;
    }

    public Long getCostMillis() {
        return costMillis;
    }

    public void setCostMillis(Long costMillis) {
        this.costMillis = costMillis;
    }

    public SqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public String getSql() {
        return sql;
    }

    public List<Object> getOrderedParms() {
        return orderedParms.stream().flatMap(param -> {
            if (param instanceof Collection<?> collectionParam) {
                return collectionParam.stream();
            }
            return Stream.of(param);
        }).toList();
    }

    public void setInterceptorEnabled(Boolean interceptorEnabled) {
        this.interceptorEnabled = interceptorEnabled;
    }

    public Boolean getInterceptorEnabled() {
        return interceptorEnabled;
    }

    /**
     * 格式化真实 SQL
     */
    public String formatSql() {
        if (sql == null || sql.isEmpty()) {
            return sql;
        }

        // 获取展开后的参数列表
        List<Object> params = getOrderedParms();
        if (params == null || params.isEmpty()) {
            return sql;
        }

        StringBuilder sb = new StringBuilder();
        int paramIndex = 0;
        int sqlLength = sql.length();

        for (int i = 0; i < sqlLength; i++) {
            char c = sql.charAt(i);
            if (c == '?' && paramIndex < params.size()) {
                // 获取当前参数并格式化
                Object param = params.get(paramIndex++);
                sb.append(Sql4jUtil.formatVal(param));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "SqlInfo{" +
                "sqlType=" + sqlType +
                ", sql='" + sql + '\'' +
                ", orderedPlaceholder=" + orderedParms +
                ", costMillis=" + costMillis +
                '}';
    }
}
