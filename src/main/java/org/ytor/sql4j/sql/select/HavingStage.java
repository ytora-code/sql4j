package org.ytor.sql4j.sql.select;

import org.ytor.sql4j.func.SFunction;
import org.ytor.sql4j.sql.*;
import org.ytor.sql4j.enums.OrderType;

import java.util.List;
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
        return getSelectBuilder().getSQLHelper().getSqlExecutionEngine().executeQuery(getSelectBuilder().getTranslator().translate(getSelectBuilder())).toBeans(clazz);
    }
}
