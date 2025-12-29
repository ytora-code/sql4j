package xyz.ytora.sql4j.orm.querygen.support;

import xyz.ytora.sql4j.enums.OrderType;
import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.orm.querygen.AbsQueryExplain;
import xyz.ytora.sql4j.orm.querygen.token.QueryToken;
import xyz.ytora.sql4j.sql.OrderItem;
import xyz.ytora.sql4j.sql.select.OrderByStage;
import xyz.ytora.sql4j.sql.select.SelectBuilder;

/**
 * 排序字段，固定字段名称：orderCol，↑表示升序，↓表示降序，orderCol=id↑,userName↓ 表示先按id升序，再按userName降序
 */
public class OrderCol extends AbsQueryExplain {

    @Override
    public Boolean isMatch(QueryToken token) {
        return token.getKey().equals("orderCol");
    }

    @Override
    public SelectBuilder apply(SelectBuilder selectBuilder, QueryToken token) {
        return positiveOrNegate(selectBuilder, token);
    }

    @Override
    protected SelectBuilder positive(SelectBuilder selectBuilder, QueryToken token) {
        String[] orderCols = token.getValue().split(",");
        OrderByStage orderByStage = null;
        for (String orderCol : orderCols) {
            String colName = orderCol.substring(0, orderCol.length() - 1);
            OrderType orderType;
            if (orderCol.endsWith("↑")) {
                orderType = OrderType.ASC;
            } else if (orderCol.endsWith("↓")) {
                orderType = OrderType.DESC;
            }
            // 默认升序
            else {
                colName = orderCol;
                orderType = OrderType.ASC;
            }
            OrderItem orderItem = new OrderItem(Raw.of(colName), orderType);
            if (orderByStage == null) {
                orderByStage = new OrderByStage(selectBuilder, orderItem);
            } else {
                orderByStage = orderByStage.orderBy(orderItem);
            }
        }
        selectBuilder.setOrderByStage(orderByStage);
        return selectBuilder;
    }

    @Override
    protected SelectBuilder negate(SelectBuilder selectBuilder, QueryToken token) {
        String[] orderCols = token.getValue().split(",");
        OrderByStage orderByStage = null;
        for (String orderCol : orderCols) {
            String colName = orderCol.substring(0, orderCol.length() - 2);
            OrderType orderType;
            if (orderCol.endsWith("↓")) {
                orderType = OrderType.ASC;
            } else if (orderCol.endsWith("↑")) {
                orderType = OrderType.DESC;
            }
            // 默认升序
            else {
                colName = orderCol;
                orderType = OrderType.DESC;
            }
            OrderItem orderItem = new OrderItem(Raw.of(colName), orderType);
            if (orderByStage == null) {
                orderByStage = new OrderByStage(selectBuilder, orderItem);
            } else {
                orderByStage = orderByStage.orderBy(orderItem);
            }
        }
        selectBuilder.setOrderByStage(orderByStage);
        return selectBuilder;
    }

    @Override
    public Integer getOrder() {
        return 1;
    }
}
