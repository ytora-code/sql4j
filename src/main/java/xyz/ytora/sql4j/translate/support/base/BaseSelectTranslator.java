package xyz.ytora.sql4j.translate.support.base;

import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.enums.SqlType;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.select.*;
import xyz.ytora.sql4j.translate.ISelectTranslator;
import xyz.ytora.sql4j.util.LambdaUtil;
import xyz.ytora.sql4j.util.TableUtil;
import xyz.ytora.ytool.classcache.ClassCache;
import xyz.ytora.ytool.classcache.classmeta.ClassMetadata;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.str.Strs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
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
        StringJoiner joiner = new StringJoiner(", ");
        // 1.1 如果指定了查询字段
        List<SFunction<?, ?>> selectColumns = builder.getSelectStage().getSelectColumns();
        for (SFunction<?, ?> f : selectColumns) {
            // 函数字段
            if (f instanceof SQLFunc func) {
                func.addAliasRegister(builder);
                String column = func.getValue();
                String as = func.as();
                if (Strs.isNotEmpty(as)) {
                    column = column + " AS " + as;
                }
                joiner.add(column);
            }
            // 普通表字段
            else {
                joiner.add(LambdaUtil.parseColumn(f, builder));
            }
        }

        // 1.2 如果要查整张表
        for (Class<?> table : builder.getSelectStage().getTableColumns()) {
            StringJoiner sj = parseGetter(table, builder);
            joiner.merge(sj);
        }

        if (joiner.length() == 0) {
            joiner.add("*");
        }
        String selectColumnStr = joiner.toString();
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
            StringJoiner groupByColumnStr = new StringJoiner(", ");

            List<SFunction<?, ?>> groupByColumns = groupByStage.getGroupColumn();
            if (groupByColumns != null && !groupByColumns.isEmpty()) {
                for (SFunction<?, ?> groupByColumn : groupByColumns) {
                    String column = LambdaUtil.parseColumn(groupByColumn, builder);
                    groupByColumnStr.add(column);
                }
            }
            List<Class<?>> groupClasses = groupByStage.getGroupClasses();
            if (groupClasses != null && !groupClasses.isEmpty()) {
                for (Class<?> groupClass : groupClasses) {
                    StringJoiner sj = parseGetter(groupClass, builder);
                    groupByColumnStr.merge(sj);
                }
            }

            if (groupByColumnStr.length() > 0) {
                sql.append("GROUP BY ").append(groupByColumnStr).append(' ');
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

    /**
     * 解析出 clazz 里面的getter
     */
    private StringJoiner parseGetter(Class<?> clazz, SelectBuilder builder) {
        StringJoiner joiner = new StringJoiner(", ");
        ClassMetadata<?> classMetadata = ClassCache.get(clazz);
        // 获取 getter 方法
        List<MethodMetadata> mmds = classMetadata.getMethods(m -> (m.getName().startsWith("get") || m.getName().startsWith("is")) && m.parameters().isEmpty());
        for (MethodMetadata mmd : mmds) {
            FieldMetadata fieldMetadata = mmd.toField();
            // 判断最终的字段名称
            Column anno = fieldMetadata.getAnnotation(Column.class);
            StringBuilder alias = new StringBuilder();
            if (!builder.single()) {
                alias.append(builder.getAlias(clazz)).append('.');
            }
            if (anno != null && !anno.value().isEmpty()) {
                alias.append(anno.value());
            } else {
                alias.append(Strs.toUnderline(fieldMetadata.getName()));
            }
            joiner.add(alias.toString());
        }
        return joiner;
    }
}
