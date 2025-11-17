package org.ytor.sql4j.sql.select;

import org.ytor.sql4j.enums.JoinType;
import org.ytor.sql4j.enums.OrderType;
import org.ytor.sql4j.sql.ConditionExpressionBuilder;
import org.ytor.sql4j.sql.OrderItem;
import org.ytor.sql4j.func.SFunction;
import org.ytor.sql4j.sql.SqlInfo;

import java.util.List;
import java.util.function.Consumer;

/**
 * FROM 阶段
 */
public class FromStage extends AbsSelect implements SelectEndStage {

    /**
     * 主表
     */
    private final Class<?> mainTable;

    public FromStage(SelectBuilder selectBuilder, Class<?> mainTable) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setFromBuilder(this);
        getSelectBuilder().addAlias(mainTable);
        this.mainTable = mainTable;
    }

    /**
     * FROM 后可能是 LEFT JOIN 子句
     */
    public JoinStage leftJoin(Class<?> joinTable, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.LEFT_JOIN, joinTable, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 RIGHT JOIN 子句
     */
    public JoinStage rightJoin(Class<?> joinTable, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.RIGHT_JOIN, joinTable, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 INNER JOIN 子句
     */
    public JoinStage innerJoin(Class<?> joinTable, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.INNER_JOIN, joinTable, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 WHERE 子句
     */
    public SelectWhereStage where(Consumer<ConditionExpressionBuilder> where) {
        return new SelectWhereStage(getSelectBuilder(), where);
    }

    /**
     * WHERE 后可能是 GROUP BY 子句
     */
    public <T> GroupByStage groupBy(SFunction<T, ?> groupColumn) {
        return new GroupByStage(getSelectBuilder(), groupColumn);
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
     * FROM 后可能结束
     */
    public SqlInfo end() {
        return getSelectBuilder().getTranslator().translate(getSelectBuilder());
    }

    public Class<?> getMainTable() {
        return mainTable;
    }

    @Override
    public <T> List<T> submit(Class<T> clazz) {
        return getSelectBuilder().getSQLHelper().getSqlExecutionEngine().executeQuery(getSelectBuilder().getTranslator().translate(getSelectBuilder())).toBeans(clazz);
    }
}
