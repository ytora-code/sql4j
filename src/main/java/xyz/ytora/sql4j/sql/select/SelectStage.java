package xyz.ytora.sql4j.sql.select;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * SELECT 阶段
 */
public class SelectStage extends AbsSelect implements SelectEndStage {

    /**
     * SELECT 查询字段
     */
    private final List<SFunction<?, ?>> selectColumns = new ArrayList<>();

    /**
     * SELECT 查询指定表的全部字段
     */
    private final List<Class<?>> tableColumns = new ArrayList<>();

    public <T> SelectStage(SelectBuilder selectBuilder, SFunction<T, ?> selectColumns) {
        setSelectBuilder(selectBuilder);
        selectBuilder.setSelectStage(this);
        this.selectColumns.add(selectColumns);
    }

    public SelectStage(SelectBuilder selectBuilder, List<SFunction<?, ?>> selectColumns) {
        setSelectBuilder(selectBuilder);
        selectBuilder.setSelectStage(this);
        this.selectColumns.addAll(selectColumns);
    }

    public SelectStage(SelectBuilder selectBuilder, Class<?> tableColumns) {
        setSelectBuilder(selectBuilder);
        selectBuilder.setSelectStage(this);
        this.tableColumns.add(tableColumns);
    }

    /**
     * SELECT 后可能继续 SELECT
     */
    @SafeVarargs
    public final <T> SelectStage select(SFunction<T, ?>... columns) {
        Collections.addAll(selectColumns, columns);
        return this;
    }

    /**
     * SELECT 后可能继续 SELECT
     */
    public SelectStage select(Class<?> clazz) {
        Collections.addAll(this.tableColumns, clazz);
        return this;
    }

    /**
     * SELECT 后可能进入 FROM 阶段
     */
    public <T> FromStage from(Class<T> table) {
        return new FromStage(getSelectBuilder(), table);
    }

    /**
     * SELECT 后可能进入 FROM 阶段
     */
    public <T> FromStage from(String table) {
        return new FromStage(getSelectBuilder(), table);
    }

    /**
     * SELECT 后可能进入 FROM 阶段（子查询）
     */
    public <T> FromStage from(AbsSelect subSelect) {
        subSelect.getSelectBuilder().isSub();
        return new FromStage(getSelectBuilder(), subSelect);
    }

    /**
     * SELECT 后可能结束
     */
    public SqlInfo end() {
        return getSelectBuilder().getTranslator().translate(getSelectBuilder());
    }

    public List<SFunction<?, ?>> getSelectColumns() {
        return selectColumns;
    }

    public List<Class<?>> getTableColumns() {
        return tableColumns;
    }

    @Override
    public <T> List<T> submit(Class<T> clazz) {
        return getSelectBuilder().getSQLHelper().getSqlExecutionEngine().executeQuery(getSelectBuilder().getTranslator().translate(getSelectBuilder())).toBeans(clazz);
    }

    @Override
    public List<Map<String, Object>> submit() {
        return getSelectBuilder().getSQLHelper().getSqlExecutionEngine().executeQuery(getSelectBuilder().getTranslator().translate(getSelectBuilder())).toBeans();
    }
}
