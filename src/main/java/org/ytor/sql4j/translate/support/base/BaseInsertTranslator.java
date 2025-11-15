package org.ytor.sql4j.translate.support.base;

import org.ytor.sql4j.Sql4JException;
import org.ytor.sql4j.enums.SqlType;
import org.ytor.sql4j.sql.SFunction;
import org.ytor.sql4j.sql.SqlInfo;
import org.ytor.sql4j.sql.Wrapper;
import org.ytor.sql4j.sql.insert.InsertBuilder;
import org.ytor.sql4j.translate.IInsertTranslator;
import org.ytor.sql4j.util.LambdaUtil;
import org.ytor.sql4j.util.TableUtil;

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

        // 2. INSERT INTO 字段
        List<SFunction<?, ?>> columns = builder.getIntoStage().getInsertedColumn();
        if (columns == null || columns.isEmpty()) {
            throw new Sql4JException("翻译SQL时出错：INSERT时必须指定COLUMNS");
        }
        String columnStr = columns.stream()
                .map(column -> LambdaUtil.parseMethodName(LambdaUtil.serializedLambda(column)))
                .collect(Collectors.joining(", "));
        sql.append("(").append(columnStr).append(") ");

        // 3. VALUES 部分
        sql.append("VALUES ");
        List<List<Object>> valuesList = builder.getValuesStage().getInsertedDataList();
        if (valuesList == null || valuesList.isEmpty()) {
            throw new Sql4JException("翻译SQL时出错：INSERT时必须指定VALUE");
        }
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

        return new SqlInfo(SqlType.INSERT, sql.toString(), orderedParms);
    }
}
