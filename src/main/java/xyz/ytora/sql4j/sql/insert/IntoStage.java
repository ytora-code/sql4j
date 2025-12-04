package xyz.ytora.sql4j.sql.insert;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.select.AbsSelect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * INTO 阶段，指定要插入的字段
 */
public class IntoStage extends AbsInsert {

    private final List<SFunction<?, ?>> insertedColumn = new ArrayList<>();

    public <T> IntoStage(InsertBuilder insertBuilder, Collection<SFunction<T, ?>> insertedColumn) {
        setInsertBuilder(insertBuilder);
        getInsertBuilder().setIntoStage(this);
        this.insertedColumn.addAll(insertedColumn);
    }

    /**
     * INTO 后面是 VALUE
     */
    public ValuesStage value(Object... insertedData) {
        return new ValuesStage(getInsertBuilder(), Arrays.asList(insertedData), insertedColumn.size());
    }

    /**
     * INTO 后面是 VALUE
     */
    public ValuesStage value(List<Object> insertedData) {
        return new ValuesStage(getInsertBuilder(), insertedData, insertedColumn.size());
    }

    /**
     * INTO 后面是 VALUE
     */
    public ValuesStage values(List<List<Object>> insertedDataList) {
        return new ValuesStage(getInsertBuilder(), new ArrayList<>(), insertedColumn.size()).values(insertedDataList);
    }

    /**
     * 将 SELECT 的查询结果集作为插入的数据
     */
    public SelectValueStage values(AbsSelect subSelect) {
        subSelect.getSelectBuilder().isSub();
        return new SelectValueStage(getInsertBuilder(), subSelect);
    }

    public List<SFunction<?, ?>> getInsertedColumn() {
        return insertedColumn;
    }
}
