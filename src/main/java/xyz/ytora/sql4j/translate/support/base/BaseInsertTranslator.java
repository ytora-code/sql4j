package xyz.ytora.sql4j.translate.support.base;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.enums.SqlType;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.Wrapper;
import xyz.ytora.sql4j.sql.insert.InsertBuilder;
import xyz.ytora.sql4j.sql.insert.SelectValueStage;
import xyz.ytora.sql4j.sql.insert.ValuesStage;
import xyz.ytora.sql4j.sql.select.AbsSelect;
import xyz.ytora.sql4j.translate.IInsertTranslator;
import xyz.ytora.sql4j.util.LambdaUtil;
import xyz.ytora.sql4j.util.TableUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * INSERT 翻译器
 */
public class BaseInsertTranslator implements IInsertTranslator {

    @Override
    public SqlInfo translate(InsertBuilder builder) {
        StringBuilder sql = new StringBuilder();
        List<Object> orderedParms = new ArrayList<>();

        // 1. INSERT INTO 表
        Class<?> table = builder.getInsertStage().getTable();
        if (table == null) {
            throw new Sql4JException("翻译SQL时出错：INSERT时必须指定TABLE");
        }
        String tableName = TableUtil.parseTableNameFromClass(table);
        sql.append("INSERT INTO ").append(tableName).append(' ');

        // 需要判断插入类型
        ValuesStage valuesStage = builder.getValuesStage();
        SelectValueStage selectValueStage = builder.getSelectValueStage();
        if (valuesStage != null) {
            // 2. INSERT INTO 字段
            List<SFunction<?, ?>> columns = builder.getIntoStage().getInsertedColumn();
            if (columns != null && !columns.isEmpty()) {
                String columnStr = columns.stream()
                        .map(column -> LambdaUtil.parseColumn(column, null))
                        .collect(Collectors.joining(", "));
                sql.append("(").append(columnStr).append(") ");
            }

            // 3. VALUES 部分
            List<List<Object>> valuesList = valuesStage.getInsertedDataList();
            if (valuesList == null || valuesList.isEmpty()) {
                throw new Sql4JException("翻译SQL时出错：INSERT时必须指定VALUE");
            }
            sql.append("VALUES ");
            List<String> valuesExpression = new ArrayList<>();
            for (List<Object> valueList : valuesList) {
                StringBuilder valueStr = new StringBuilder();
                valueStr.append("(");
                List<String> values = new ArrayList<>();
                for (Object value : valueList) {
                    if (value instanceof Wrapper) {
                        Wrapper wrapperValue = (Wrapper) value;
                        String sourceValue = wrapperValue.getRealValue();
                        values.add(sourceValue);
                    } else {
                        values.add("?");
                        orderedParms.add(value);
                    }
                }
                valueStr.append(String.join(", ", values));
                valueStr.append(")");
                valuesExpression.add(valueStr.toString());
            }
            sql.append(String.join(", ", valuesExpression));
        } else if (selectValueStage != null) {
            // 2. INSERT INTO 字段
            // INSERT INTO SELECT 时，强行限制插入字段和查询字段必须一致
            List<SFunction<?, ?>> columns = selectValueStage.getSubSelect().getSelectBuilder().getSelectStage().getSelectColumns();
            if (columns == null || columns.isEmpty()) {
                throw new Sql4JException("INSERT INTO SELECT 时，必须指定 SELECT 字段");
            }
            String columnStr = columns.stream()
                    .map(column -> LambdaUtil.parseColumn(column, null))
                    .collect(Collectors.joining(", "));
            sql.append("(").append(columnStr).append(") ");

            AbsSelect subSelect = selectValueStage.getSubSelect();
            if (subSelect == null) {
                throw new Sql4JException("翻译SQL时出错：INSERT时子查询不能为空");
            }
            sql.append('(');
            SqlInfo sqlInfo = subSelect.getSelectBuilder().getTranslator().translate(subSelect.getSelectBuilder());
            sql.append(sqlInfo.getSql());
            orderedParms.addAll(sqlInfo.getOrderedParms());
            sql.append(')');
        }


        return new SqlInfo(builder, SqlType.INSERT, sql.toString(), orderedParms);
    }
}
