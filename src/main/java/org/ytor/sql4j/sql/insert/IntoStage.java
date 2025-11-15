package org.ytor.sql4j.sql.insert;

import org.ytor.sql4j.sql.SFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * INTO 阶段，指定要插入的字段
 */
public class IntoStage extends AbsInsert {

    private final List<SFunction<?, ?>> insertedColumn;

    public IntoStage(InsertBuilder insertBuilder, List<SFunction<?, ?>> insertedColumn) {
        setInsertBuilder(insertBuilder);
        getInsertBuilder().setIntoStage(this);
        this.insertedColumn = insertedColumn;
    }

    /**
     * INTO 后面是 VALUE
     */
    public ValuesStage value(Object... insertedData) {
        return new ValuesStage(getInsertBuilder(), Arrays.asList(insertedData));
    }

    /**
     * INTO 后面是 VALUE
     */
    public ValuesStage value(List<Object> insertedData) {
        return new ValuesStage(getInsertBuilder(), insertedData);
    }

    /**
     * INTO 后面是 VALUE
     */
    public ValuesStage values(List<List<Object>> insertedDataList) {
        return new ValuesStage(getInsertBuilder(), new ArrayList<>()).values(insertedDataList);
    }

    public List<SFunction<?, ?>> getInsertedColumn() {
        return insertedColumn;
    }
}
