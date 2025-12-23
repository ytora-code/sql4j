package xyz.ytora.sql4j.translate.support.base;

import xyz.ytora.sql4j.enums.SqlType;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.delete.DeleteBuilder;
import xyz.ytora.sql4j.sql.delete.DeleteWhereStage;
import xyz.ytora.sql4j.translate.IDeleteTranslator;

import java.util.ArrayList;
import java.util.List;

/**
 * DELETE 翻译器
 */
public class BaseDeleteTranslator implements IDeleteTranslator {

    @Override
    public SqlInfo translate(DeleteBuilder builder) {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        List<Object> orderedParms = new ArrayList<>();

        // 1. 目标表
        String tableName = builder.getFromStage().getTableName();
        sql.append(tableName).append(' ');

        // 2. 删除条件
        DeleteWhereStage whereStage = builder.getWhereStage();
        if (whereStage != null) {
            ConditionExpressionBuilder whereExpressionBuilder = whereStage.getWhere();
            if (whereExpressionBuilder != null) {
                String whereExpression = whereExpressionBuilder.build();
                if (!whereExpression.isEmpty()) {
                    sql.append("WHERE ").append(whereExpression).append(' ');
                    // 收集 WHERE 子句的参数
                    orderedParms.addAll(whereExpressionBuilder.getParams());
                }
            }
        }

        return new SqlInfo(builder, SqlType.DELETE, sql.toString(), orderedParms);
    }
}
