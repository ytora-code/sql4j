package org.ytor.sql4j.interceptor.support;

import org.ytor.sql4j.Sql4JException;
import org.ytor.sql4j.sql.ConditionExpressionBuilder;
import org.ytor.sql4j.sql.SqlBuilder;
import org.ytor.sql4j.sql.SqlInfo;
import org.ytor.sql4j.sql.delete.DeleteBuilder;
import org.ytor.sql4j.sql.update.UpdateBuilder;

/**
 * 防止全表更新和删除
 */
public class PreventFullTableUpdateInterceptor extends SqlInterceptorAdapter {

    @Override
    public Boolean before(SqlInfo sqlInfo) {
        SqlBuilder sqlBuilder = sqlInfo.getSqlBuilder();
        if (sqlBuilder instanceof UpdateBuilder) {
            UpdateBuilder builder = (UpdateBuilder) sqlBuilder;
            ConditionExpressionBuilder whereExpression = builder.getWhereStage().getWhereExpression();
            checkIsFullTableUpdate(whereExpression);
        } else if (sqlBuilder instanceof DeleteBuilder) {
            DeleteBuilder builder = (DeleteBuilder) sqlBuilder;
            ConditionExpressionBuilder whereExpression = builder.getWhereStage().getWhereExpression();
            checkIsFullTableUpdate(whereExpression);
        }
        return true;
    }

    private void checkIsFullTableUpdate(ConditionExpressionBuilder whereExpression) {
        if (whereExpression == null) {
            throw new Sql4JException("UPDATE 或者 DELETE 时，需要携带 WHERE 条件");
        }
    }
}
