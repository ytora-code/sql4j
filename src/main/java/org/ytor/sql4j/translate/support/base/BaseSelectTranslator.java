package org.ytor.sql4j.translate.support.base;

import org.ytor.sql4j.enums.SqlType;
import org.ytor.sql4j.func.SFunction;
import org.ytor.sql4j.func.SQLFunc;
import org.ytor.sql4j.sql.ConditionExpressionBuilder;
import org.ytor.sql4j.sql.SqlInfo;
import org.ytor.sql4j.sql.select.*;
import org.ytor.sql4j.translate.ISelectTranslator;
import org.ytor.sql4j.util.LambdaUtil;
import org.ytor.sql4j.util.TableUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * SELECT 翻译器
 */
public class BaseSelectTranslator implements ISelectTranslator {

    @Override
    public SqlInfo translate(SelectBuilder builder) {
        StringBuilder sql = new StringBuilder();
        List<Object> orderedParms = new ArrayList<>();

        // 1.SELECT 查询字段
        DistinctStage distinctStage = builder.getDistinctStage();
        List<SFunction<?, ?>> selectColumns = builder.getSelectStage().getSelectColumn();
        String selectColumnStr = selectColumns.stream().map(f -> {
            // 函数字段
            if (f instanceof SQLFunc) {
                SQLFunc func = (SQLFunc) f;
                func.addAliasRegister(builder);
                return func.getValue();
            }
            // 普通表字段
            else {
                return LambdaUtil.parseColumn(f, builder);
            }
        }).collect(Collectors.joining(", "));
        if (selectColumnStr.isEmpty()) {
            selectColumnStr = "*";
        }
        sql.append("SELECT ");
        if (distinctStage != null && !"*".equals(selectColumnStr)) {
            sql.append("DISTINCT").append(' ');
        }
        sql.append(selectColumnStr).append(' ');

        // 2.解析 FROM 子句
        FromStage fromStage = builder.getFromStage();
        if (fromStage == null) {
            return new SqlInfo(builder, SqlType.SELECT, sql.toString(), orderedParms);
        }
        sql.append("FROM ");
        Integer tableType = fromStage.getTableType();
        if (tableType == 1) {
            // 物理表
            Class<?> mainTable = fromStage.getMainTable();
            String tableName = TableUtil.parseTableNameFromClass(mainTable);
            sql.append(tableName).append(' ');
            String alias = builder.getAlias(mainTable);
            if (!builder.single()) {
                sql.append(alias).append(' ');
            }
        } else {
            // 虚拟表
            AbsSelect subSelect = fromStage.getSubSelect();
            sql.append('(');
            SqlInfo sqlInfo = subSelect.getSelectBuilder().getSQLHelper().getTranslator().translate(subSelect.getSelectBuilder());
            sql.append(sqlInfo.getSql());
            orderedParms.addAll(sqlInfo.getOrderedParms());
            sql.append(')').append(' ').append(builder.getAlias(subSelect)).append(' ');
        }

        // 3. JOIN 关联表，JOIN 子句中可能会出现占位符参数
        List<JoinStage> joinStages = builder.getJoinStages();
        if (joinStages != null && !joinStages.isEmpty()) {
            for (JoinStage join : joinStages) {
                Class<?> joinTable = join.getJoinTable();
                String joinTableName = TableUtil.parseTableNameFromClass(joinTable);
                String joinTableAliasName = builder.getAlias(joinTable);
                String joinKey = join.getJoinType().getJoinKey();
                sql.append(joinKey).append(' ').append(joinTableName).append(' ');
                if (joinTableAliasName != null) {
                    sql.append(joinTableAliasName).append(' ');
                }
                if (join.getOn() != null) {
                    ConditionExpressionBuilder onExpressionBuilder = new ConditionExpressionBuilder(builder);
                    join.getOn().accept(onExpressionBuilder);
                    String onExpression = onExpressionBuilder.build();
                    if (!onExpression.isEmpty()) {
                        sql.append("ON ").append(onExpression).append(' ');
                        // 收集 ON 子句中的参数
                        orderedParms.addAll(onExpressionBuilder.getParams());
                    }
                }
            }
        }


        // 4. WHERE 子句，WHERE 子句中可能会出现占位符参数
        SelectWhereStage selectWhereStage = builder.getWhereStage();
        if (selectWhereStage != null) {
            Consumer<ConditionExpressionBuilder> where = selectWhereStage.getWhere();
            ConditionExpressionBuilder whereExpressionBuilder = new ConditionExpressionBuilder(builder);
            where.accept(whereExpressionBuilder);
            String whereExpression = whereExpressionBuilder.build();
            if (!whereExpression.isEmpty()) {
                sql.append("WHERE ").append(whereExpression).append(' ');
                // 收集 WHERE 子句的参数
                orderedParms.addAll(whereExpressionBuilder.getParams());
            }
        }

        // 5. GROUP BY 子句
        GroupByStage groupByStage = builder.getGroupByStage();
        if (groupByStage != null) {
            List<SFunction<?, ?>> groupByColumns = groupByStage.getGroupColumn();
            if (groupByColumns != null && !groupByColumns.isEmpty()) {
                String groupByColumn = groupByColumns.stream()
                        .map(f -> LambdaUtil.parseColumn(f, builder))
                        .collect(Collectors.joining(", "));
                sql.append("GROUP BY ").append(groupByColumn).append(' ');
            }
        }

        // 6. HAVING 条件，HAVING 子句中可能会出现占位符参数
        HavingStage havingStage = builder.getHavingStage();
        if (havingStage != null) {
            ConditionExpressionBuilder havingExpressionBuilder = new ConditionExpressionBuilder(builder);
            havingStage.getHaving().accept(havingExpressionBuilder);
            String havingExpression = havingExpressionBuilder.build();
            if (!havingExpression.isEmpty()) {
                sql.append("HAVING ").append(havingExpression).append(' ');
                // 收集 HAVING 子句参数
                orderedParms.addAll(havingExpressionBuilder.getParams());
            }
        }

        // 7. ORDER BY 排序
        OrderByStage orderByStage = builder.getOrderByStage();
        if (orderByStage != null) {
            String orderExpression = orderByStage.getOrderItems().stream()
                    .map(item -> {
                        String column = LambdaUtil.parseColumn(item.getOrderColumn(), builder);
                        return column + " " + item.getOrderType().name();
                    })
                    .collect(Collectors.joining(", "));
            sql.append("ORDER BY ").append(orderExpression).append(' ');
        }

        // 8. LIMIT OFFSET
        LimitStage limitStage = builder.getLimitStage();
        if (limitStage != null) {
            sql.append("LIMIT ").append(limitStage.getLimit()).append(' ');

            OffsetStage offsetStage = builder.getOffsetStage();
            if (offsetStage != null) {
                sql.append("OFFSET ").append(offsetStage.getOffset()).append(' ');
            }
        }

        return new SqlInfo(builder, SqlType.SELECT, sql.toString(), orderedParms);
    }

}
