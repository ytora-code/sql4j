package xyz.ytora.sql4j.sql.select;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.enums.OrderType;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.OrderItem;
import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * HAVING 阶段
 */
public class HavingStage extends AbsSelect implements SelectEndStage {

    private final Consumer<ConditionExpressionBuilder> having;

    public HavingStage(SelectBuilder selectBuilder, Consumer<ConditionExpressionBuilder> having) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setHavingStage(this);
        this.having = having;
    }

    /**
     * HAVING 后可能是 ORDER BY 子句
     */
    public <T> OrderByStage orderBy(SFunction<T, ?> orderColumn, OrderType orderType) {
        return new OrderByStage(getSelectBuilder(), new OrderItem(orderColumn, orderType));
    }

    /**
     * HAVING 后可能是 LIMIT 子句
     */
    public LimitStage limit(Integer limit) {
        return new LimitStage(getSelectBuilder(), limit);
    }

    /**
     * HAVING 后可能结束
     */
    public SqlInfo end() {
        return getSelectBuilder().getTranslator().translate(getSelectBuilder());
    }

    public Consumer<ConditionExpressionBuilder> getHaving() {
        return having;
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
