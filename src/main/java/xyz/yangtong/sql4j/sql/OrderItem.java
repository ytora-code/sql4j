package xyz.yangtong.sql4j.sql;

import xyz.yangtong.sql4j.enums.OrderType;
import xyz.yangtong.sql4j.func.SFunction;

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

    public <T> OrderItem(SFunction<T, ?> orderColumn, OrderType orderType) {
        this.orderColumn = orderColumn;
        this.orderType = orderType;
    }

    public <T> void setOrderColumn(SFunction<T, ?> orderColumn) {
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
