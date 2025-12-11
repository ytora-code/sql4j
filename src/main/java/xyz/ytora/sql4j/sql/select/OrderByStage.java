package xyz.ytora.sql4j.sql.select;

import xyz.ytora.sql4j.sql.OrderItem;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.enums.OrderType;
import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ORDER BY 阶段
 */
public class OrderByStage extends AbsSelect implements SelectEndStage {

    private final List<OrderItem> orderItems = new ArrayList<>();

    public OrderByStage(SelectBuilder selectBuilder, OrderItem orderItem) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setOrderByStage(this);
        this.orderItems.add(orderItem);
    }

    public OrderByStage(SelectBuilder selectBuilder, List<OrderItem> orderItems) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setOrderByStage(this);
        this.orderItems.addAll(orderItems);
    }

    /**
     * ORDER BY 后可能继续 ORDER BY
     */
    public <T> OrderByStage orderBy(SFunction<T, ?> orderColumn, OrderType orderType) {
        return orderBy(new OrderItem(orderColumn, orderType));
    }

    /**
     * ORDER BY 后可能继续 ORDER BY
     */
    public OrderByStage orderBy(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        return this;
    }

    /**
     * ORDER BY 后可能是 LIMIT 子句
     */
    public LimitStage limit(Integer limit) {
        return new LimitStage(getSelectBuilder(), limit);
    }

    /**
     * ORDER BY 后可能结束
     */
    public SqlInfo end() {
        return getSelectBuilder().getTranslator().translate(getSelectBuilder());
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
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
