package org.ytor.sql4j.sql.select;

import org.ytor.sql4j.func.SFunction;
import org.ytor.sql4j.sql.SqlInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SELECT 阶段
 */
public class SelectStage extends AbsSelect implements SelectEndStage {

    /**
     * SELECT 查询字段
     */
    private final List<SFunction<?, ?>> selectColumn = new ArrayList<>();

    public <T> SelectStage(SelectBuilder selectBuilder, SFunction<T, ?> selectColumns) {
        setSelectBuilder(selectBuilder);
        selectBuilder.setSelectStage(this);
        this.selectColumn.add(selectColumns);
    }

    public SelectStage(SelectBuilder selectBuilder, List<SFunction<?, ?>> selectColumns) {
        setSelectBuilder(selectBuilder);
        selectBuilder.setSelectStage(this);
        this.selectColumn.addAll(selectColumns);
    }

    /**
     * SELECT 后可能继续 SELECT
     */
    @SafeVarargs
    public final <T> SelectStage select(SFunction<T, ?>... columns) {
        Collections.addAll(selectColumn, columns);
        return this;
    }

    /**
     * SELECT 后可能进入 FROM 阶段
     */
    public <T> FromStage from(Class<T> table) {
        return new FromStage(getSelectBuilder(), table);
    }

    /**
     * SELECT 后可能结束
     */
    public SqlInfo end() {
        return getSelectBuilder().getTranslator().translate(getSelectBuilder());
    }

    public List<SFunction<?, ?>> getSelectColumn() {
        return selectColumn;
    }

    @Override
    public <T> List<T> submit(Class<T> clazz) {
        return getSelectBuilder().getSQLHelper().getSqlExecutionEngine().executeQuery(getSelectBuilder().getTranslator().translate(getSelectBuilder())).toBeans(clazz);
    }
}
