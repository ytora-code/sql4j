package xyz.ytora.sql4j.translate.support.base;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.enums.SqlType;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.update.SetStage;
import xyz.ytora.sql4j.sql.update.UpdateBuilder;
import xyz.ytora.sql4j.sql.update.UpdateWhereStage;
import xyz.ytora.sql4j.translate.IUpdateTranslator;
import xyz.ytora.sql4j.util.LambdaUtil;
import xyz.ytora.sql4j.util.TableUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * UPDATE 翻译器
 */
public class BaseUpdateTranslator implements IUpdateTranslator {

    @Override
    public SqlInfo translate(UpdateBuilder builder) {
        StringBuilder sql = new StringBuilder();
        List<Object> orderedParms = new ArrayList<>();

        // 1. UPDATE 表
        Class<?> table = builder.getUpdateStage().getTable();
        if (table == null) {
            throw new Sql4JException("翻译SQL时出错：DELETE时必须指定TABLE");
        }
        String tableName = TableUtil.parseTableNameFromClass(table);
        sql.append("UPDATE ").append(tableName).append(' ');

        // 1. SET 阶段：构建字段更新部分
        SetStage setStage = builder.getSetStage();
        Map<SFunction<?, ?>, Object> columnValueMap = setStage.getUpdatedColumnValueMapper();
        List<String> setClauseList = new ArrayList<>();
        // 遍历字段和值的映射，生成 SET 子句
        for (Map.Entry<SFunction<?, ?>, Object> entry : columnValueMap.entrySet()) {
            String columnName = LambdaUtil.parseColumn(entry.getKey(), null);
            // 生成 "column = ?" 形式的语句
            setClauseList.add(columnName + " = ?");
            // 记录参数
            orderedParms.add(entry.getValue());
        }

        sql.append("SET ")
                .append(String.join(", ", setClauseList))
                .append(" ");

        // 2. WHERE 阶段：构建 WHERE 子句（如果存在）
        UpdateWhereStage whereStage = builder.getWhereStage();
        if (whereStage != null) {
            // 使用 ConditionExpressionBuilder 构建 WHERE 子句
            ConditionExpressionBuilder conditionBuilder = new ConditionExpressionBuilder(builder);
            whereStage.getWhere().accept(conditionBuilder);
            whereStage.setWhereExpression(conditionBuilder);

            sql.append("WHERE ").append(conditionBuilder.build());
            orderedParms.addAll(conditionBuilder.getParams());
        }

        return new SqlInfo(builder, SqlType.UPDATE, sql.toString(), orderedParms);
    }
}
