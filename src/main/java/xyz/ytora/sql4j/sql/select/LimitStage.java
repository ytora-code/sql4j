package xyz.ytora.sql4j.sql.select;

import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.List;
import java.util.Map;

/**
 * LIMIT 阶段
 */
public class LimitStage extends AbsSelect implements SelectEndStage {

    private final Integer limit;

    public LimitStage(SelectBuilder selectBuilder, Integer limit) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setLimitStage(this);
        this.limit = limit;
    }

    /**
     * LIMIT 后可能是 OFFSET 子句
     */
    public OffsetStage offset(Integer offset) {
        return new OffsetStage(getSelectBuilder(), offset);
    }

    /**
     * LIMIT 后可能结束
     */
    public SqlInfo end() {
        return getSelectBuilder().getTranslator().translate(getSelectBuilder());
    }

    public Integer getLimit() {
        return limit;
    }

    @Override
    public <T> List<T> submit(Class<T> clazz) {
        return getSelectBuilder().getSQLHelper().getSqlExecutionEngine().executeSelect(getSelectBuilder().getTranslator().translate(getSelectBuilder())).toBeans(clazz);
    }

    @Override
    public List<Map<String, Object>> submit() {
        return getSelectBuilder().getSQLHelper().getSqlExecutionEngine().executeSelect(getSelectBuilder().getTranslator().translate(getSelectBuilder())).toBeans();
    }
}
