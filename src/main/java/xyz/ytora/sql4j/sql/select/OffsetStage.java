package xyz.ytora.sql4j.sql.select;

import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.List;
import java.util.Map;

/**
 * OFFSET 阶段
 */
public class OffsetStage extends AbsSelect implements SelectEndStage {

    private final Integer offset;

    public OffsetStage(SelectBuilder selectBuilder, Integer offset) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setOffsetStage(this);
        this.offset = offset;
    }

    /**
     * OFFSET 后可能结束
     */
    public SqlInfo end() {
        return getSelectBuilder().getTranslator().translate(getSelectBuilder());
    }

    public Integer getOffset() {
        return offset;
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
