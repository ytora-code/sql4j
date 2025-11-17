package org.ytor.sql4j.sql.select;

import org.ytor.sql4j.func.SFunction;
import org.ytor.sql4j.sql.*;
import org.ytor.sql4j.enums.OrderType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * WHERE 阶段
 */
public class SelectWhereStage extends AbsSelect implements SelectEndStage {

    private final Consumer<ConditionExpressionBuilder> where;

    public SelectWhereStage(SelectBuilder selectBuilder, Consumer<ConditionExpressionBuilder> where) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setWhereStage(this);
        this.where = where;
    }

    /**
     * WHERE 后可能是 GROUP BY 子句
     */
    @SafeVarargs
    public final <T> GroupByStage groupBy(SFunction<T, ?>... groupColumns) {
        return new GroupByStage(getSelectBuilder(), Arrays.asList(groupColumns));
    }

    /**
     * WHERE 后可能是 ORDER BY 子句
     */
    public <T> OrderByStage orderBy(SFunction<T, ?> orderColumn, OrderType orderType) {
        return new OrderByStage(getSelectBuilder(), new OrderItem(orderColumn, orderType));
    }

    /**
     * WHERE 后可能是 LIMIT 子句
     */
    public LimitStage limit(Integer limit) {
        return new LimitStage(getSelectBuilder(), limit);
    }

    /**
     * WHERE 后可能结束
     */
    public SqlInfo end() {
        return getSelectBuilder().getTranslator().translate(getSelectBuilder());
    }

    public Consumer<ConditionExpressionBuilder> getWhere() {
        return where;
    }

    @Override
    public <T> List<T> submit(Class<T> clazz) {
        return getSelectBuilder().getSQLHelper().getSqlExecutionEngine().executeQuery(getSelectBuilder().getTranslator().translate(getSelectBuilder())).toBeans(clazz);
    }
}
