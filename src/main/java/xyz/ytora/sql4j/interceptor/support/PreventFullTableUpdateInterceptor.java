package xyz.ytora.sql4j.interceptor.support;

import xyz.ytora.sql4j.interceptor.SqlInterceptorAdapter;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.SqlBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.delete.DeleteBuilder;
import xyz.ytora.sql4j.sql.update.UpdateBuilder;
import xyz.ytora.ytool.str.Strs;

/**
 * 防止全表更新和删除
 */
public class PreventFullTableUpdateInterceptor extends SqlInterceptorAdapter {

    @Override
    public Boolean before(SqlInfo sqlInfo) {
        SqlBuilder sqlBuilder = sqlInfo.getSqlBuilder();
        ConditionExpressionBuilder whereExpression;
        if (sqlBuilder instanceof UpdateBuilder builder) {
            whereExpression = builder.getWhereStage().getWhere();
        } else if (sqlBuilder instanceof DeleteBuilder builder) {
            whereExpression = builder.getWhereStage().getWhere();
        } else {
            return true;
        }

        if (Strs.isEmpty(whereExpression.build())) {
            sqlBuilder.getSQLHelper().getLogger().error(sqlInfo.getSqlType().name() + "操作时 WHERE 子句不能为空");
            return false;
        }
        return true;
    }
}
