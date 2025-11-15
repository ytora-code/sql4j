package org.ytor.sql4j.sql.select;

import org.ytor.sql4j.sql.*;
import org.ytor.sql4j.enums.OrderType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * GROUP BY 阶段
 */
public class GroupByStage extends AbsSelect {

    private final List<SFunction<?, ?>> groupColumn = new ArrayList<>();

    /**
     * 分组
     * @param selectBuilder SELECT 构造器
     * @param groupColumn 分组字段
     */
    public GroupByStage(SelectBuilder selectBuilder, SFunction<?, ?> groupColumn) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setGroupByStage(this);
        this.groupColumn.add(groupColumn);
    }

    /**
     * 分组
     * @param selectBuilder SELECT 构造器
     * @param groupColumn 分组字段
     */
    public GroupByStage(SelectBuilder selectBuilder, List<SFunction<?, ?>> groupColumn) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setGroupByStage(this);
        this.groupColumn.addAll(groupColumn);
    }

    /**
     * GROUP BY 后可能是 HAVING 子句
     */
    public HavingStage orderBy(Consumer<ConditionExpressionBuilder> where) {
        return new HavingStage(getSelectBuilder(), where);
    }

    /**
     * GROUP BY 后可能是 ORDER BY 子句
     */
    public OrderByStage orderBy(SFunction<?, ?> orderColumn, OrderType orderType) {
        return new OrderByStage(getSelectBuilder(), new OrderItem(orderColumn, orderType));
    }

    /**
     * GROUP BY 后可能是 LIMIT 子句
     */
    public LimitStage limit(Long limit) {
        return new LimitStage(getSelectBuilder(), limit);
    }

    /**
     * GROUP BY 后可能结束
     */
    public SqlInfo end() {
        return getSelectBuilder().getTranslator().translate(getSelectBuilder());
    }

    public List<SFunction<?, ?>> getGroupColumn() {
        return groupColumn;
    }

}
