package xyz.yangtong.sql4j.sql.select;

import xyz.yangtong.sql4j.func.SFunction;
import xyz.yangtong.sql4j.enums.OrderType;
import xyz.yangtong.sql4j.sql.ConditionExpressionBuilder;
import xyz.yangtong.sql4j.sql.OrderItem;
import xyz.yangtong.sql4j.sql.SqlInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * GROUP BY 阶段
 */
public class GroupByStage extends AbsSelect implements SelectEndStage {

    private final List<SFunction<?, ?>> groupColumn = new ArrayList<>();

    private final List<Class<?>> groupClasses = new ArrayList<>();

    /**
     * 分组
     * @param selectBuilder SELECT 构造器
     * @param groupColumn 分组字段
     */
    public <T> GroupByStage(SelectBuilder selectBuilder, SFunction<T, ?> groupColumn) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setGroupByStage(this);
        this.groupColumn.add(groupColumn);
    }

    /**
     * 分组
     * @param selectBuilder SELECT 构造器
     * @param groupColumn 分组字段
     */
    public <T> GroupByStage(SelectBuilder selectBuilder, List<SFunction<T, ?>> groupColumn) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setGroupByStage(this);
        this.groupColumn.addAll(groupColumn);
    }

    /**
     * 分组
     * @param selectBuilder SELECT 构造器
     * @param groupClass 分组字段
     */
    public GroupByStage(SelectBuilder selectBuilder, Class<?> groupClass) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setGroupByStage(this);
        this.groupClasses.add(groupClass);
    }

    /**
     * GROUP BY 后可能是 HAVING 子句
     */
    public HavingStage having(Consumer<ConditionExpressionBuilder> where) {
        return new HavingStage(getSelectBuilder(), where);
    }

    /**
     * GROUP BY 后可能是 ORDER BY 子句
     */
    public <T> OrderByStage orderBy(SFunction<T, ?> orderColumn, OrderType orderType) {
        return new OrderByStage(getSelectBuilder(), new OrderItem(orderColumn, orderType));
    }

    /**
     * GROUP BY 后可能是 LIMIT 子句
     */
    public LimitStage limit(Integer limit) {
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

    public List<Class<?>> getGroupClasses() {
        return groupClasses;
    }

    @Override
    public <T> List<T> submit(Class<T> clazz) {
        return getSelectBuilder().getSQLHelper().getSqlExecutionEngine().executeQuery(getSelectBuilder().getTranslator().translate(getSelectBuilder())).toBeans(clazz);
    }
}
