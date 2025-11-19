package org.ytor.sql4j.translate.support.base;

import org.ytor.sql4j.Sql4JException;
import org.ytor.sql4j.enums.SqlType;
import org.ytor.sql4j.sql.ConditionExpressionBuilder;
import org.ytor.sql4j.sql.SqlInfo;
import org.ytor.sql4j.sql.delete.DeleteBuilder;
import org.ytor.sql4j.sql.delete.DeleteWhereStage;
import org.ytor.sql4j.translate.IDeleteTranslator;
import org.ytor.sql4j.util.TableUtil;

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
        Class<?> table = builder.getFromStage().getTable();
        if (table == null) {
            throw new Sql4JException("翻译SQL时出错：DELETE时必须指定TABLE");
        }
        String tableName = TableUtil.parseTableNameFromClass(table);
        sql.append(tableName).append(' ');

        // 2. 删除条件
        DeleteWhereStage whereStage = builder.getWhereStage();
        if (whereStage != null) {
            ConditionExpressionBuilder whereExpressionBuilder = new ConditionExpressionBuilder(builder);
            whereStage.getWhere().accept(whereExpressionBuilder);
            whereStage.setWhereExpression(whereExpressionBuilder);

            String whereExpression = whereExpressionBuilder.build();
            if (!whereExpression.isEmpty()) {
                sql.append("WHERE ").append(whereExpression).append(' ');
                // 收集 WHERE 子句的参数
                orderedParms.addAll(whereExpressionBuilder.getParams());
            }
        }

        return new SqlInfo(builder, SqlType.DELETE, sql.toString(), orderedParms);
    }
}
