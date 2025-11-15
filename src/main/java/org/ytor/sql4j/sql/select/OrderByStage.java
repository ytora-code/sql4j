package org.ytor.sql4j.sql.select;

import org.ytor.sql4j.sql.OrderItem;
import org.ytor.sql4j.sql.SFunction;
import org.ytor.sql4j.enums.OrderType;
import org.ytor.sql4j.sql.SqlInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * ORDER BY 阶段
 */
public class OrderByStage extends AbsSelect {

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
    public OrderByStage orderBy(SFunction<?, ?> orderColumn, OrderType orderType) {
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
    public LimitStage limit(Long limit) {
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


}
