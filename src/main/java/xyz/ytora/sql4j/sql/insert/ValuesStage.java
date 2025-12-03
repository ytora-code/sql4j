package xyz.ytora.sql4j.sql.insert;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.sql.AbsSql;
import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * VALUE 阶段，指定要插入的数据
 */
public class ValuesStage extends AbsInsert implements InsertEndStage {

    /**
     * 可能插入多条数据
     */
    private final List<List<Object>> insertedDataList = new ArrayList<>();

    /**
     * 字段数量
     */
    private final Integer count;

    public ValuesStage(InsertBuilder insertBuilder, List<Object> insertedData, Integer count) {
        setInsertBuilder(insertBuilder);
        getInsertBuilder().setValuesStage(this);
        if (insertedData != null && !insertedData.isEmpty()) {
            for (Object datum : insertedData) {
                check(datum);
            }
            this.insertedDataList.add(insertedData);
        }
        this.count = count;
    }

    /**
     * VALUE 后面可能继续 VALUE
     */
    public ValuesStage value(Object... insertedData) {
        return value(Arrays.asList(insertedData));
    }

    /**
     * VALUE 后面可能继续 VALUE
     */
    public ValuesStage value(List<Object> insertedData) {
        if (insertedData.size() != count) {
            throw new Sql4JException("插入数据的长度【" + insertedData.size() + "】与指定的字段长度【" + count + "】不匹配");
        }
        for (Object datum : insertedData) {
            check(datum);
        }
        insertedDataList.add(insertedData);
        return this;
    }

    /**
     * VALUE 后面可能继续 VALUE
     */
    public ValuesStage values(List<List<Object>> insertedDataList) {
        for (List<Object> insertedData : insertedDataList) {
            for (Object datum : insertedData) {
                check(datum);
            }
            this.value(insertedData);
        }
        return this;
    }

    /**
     * VALUES 后可能结束
     */
    public SqlInfo end() {
        return getInsertBuilder().getTranslator().translate(getInsertBuilder());
    }

    public List<List<Object>> getInsertedDataList() {
        return insertedDataList;
    }

    @Override
    public List<Object> submit() {
        return getInsertBuilder().getSQLHelper().getSqlExecutionEngine().executeInsert(getInsertBuilder().getTranslator().translate(getInsertBuilder())).getIds();
    }

    /**
     * 由于 ValuesStage 和 SelectValueStage 是两个独立的阶段类，所以 ValuesStage 不允许子查询
     */
    private void check(Object v) {
        if (v instanceof AbsSql) {
            throw new UnsupportedOperationException("非法的 INSERT 字段，该 INSERT 内部必须全部使用方法引用，当前字段类型【" + v.getClass() + "】");
        }
    }
}
