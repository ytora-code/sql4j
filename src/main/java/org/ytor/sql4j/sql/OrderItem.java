package org.ytor.sql4j.sql;

import org.ytor.sql4j.enums.OrderType;

/**
 * 排序字段
 */
public class OrderItem {

    /**
     * 排序字段
     */
    private SFunction<?, ?> orderColumn;

    /**
     * 排序类型
     */
    private OrderType orderType;

    public OrderItem(SFunction<?, ?> orderColumn, OrderType orderType) {
        this.orderColumn = orderColumn;
        this.orderType = orderType;
    }

    public void setOrderColumn(SFunction<?, ?> orderColumn) {
        this.orderColumn = orderColumn;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public SFunction<?, ?> getOrderColumn() {
        return orderColumn;
    }
}
