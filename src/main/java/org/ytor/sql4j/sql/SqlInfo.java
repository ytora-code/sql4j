package org.ytor.sql4j.sql;

import org.ytor.sql4j.enums.SqlType;

import java.util.List;

/**
 * SQL 状态
 */
public class SqlInfo {

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

    public SqlInfo(SqlBuilder sqlBuilder, SqlType sqlType, String sql, List<Object> orderedParms) {
        this.sqlBuilder = sqlBuilder;
        this.sqlType = sqlType;
        this.sql = sql.trim();
        this.orderedParms = orderedParms;
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
        return orderedParms;
    }

    @Override
    public String toString() {
        return "SqlInfo{" +
                "sqlType=" + sqlType +
                ", sql='" + sql + '\'' +
                ", orderedPlaceholder=" + orderedParms +
                '}';
    }
}
