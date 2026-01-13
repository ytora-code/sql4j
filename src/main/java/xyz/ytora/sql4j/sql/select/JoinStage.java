package xyz.ytora.sql4j.sql.select;

import xyz.ytora.sql4j.enums.JoinType;
import xyz.ytora.sql4j.enums.OrderType;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.OrderItem;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.util.Sql4jUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * JOIN 阶段
 */
public class JoinStage extends AbsSelect implements SelectEndStage {

    /**
     * 连接类型
     */
    public JoinType joinType;

    /**
     * 连接表(实体类)
     */
    private final TableInfo tableInfo;

    /**
     * 连接条件
     */
    public Consumer<ConditionExpressionBuilder> on;

    public JoinStage(JoinType joinType, Class<?> joinTable, SelectBuilder selectBuilder, Consumer<ConditionExpressionBuilder> on) {
        this.joinType = joinType;
        setSelectBuilder(selectBuilder);
        getSelectBuilder().addJoinStages(this);
        this.on = on;

        this.tableInfo = new TableInfo(1, joinTable, null, null);
        // 注册表别名
        getSelectBuilder().addAlias(tableInfo);
    }

    public JoinStage(JoinType joinType, Class<?> joinTable, String alias, SelectBuilder selectBuilder, Consumer<ConditionExpressionBuilder> on) {
        this.joinType = joinType;
        setSelectBuilder(selectBuilder);
        getSelectBuilder().addJoinStages(this);
        this.on = on;

        this.tableInfo = new TableInfo(1, joinTable, null, null);
        // 注册表别名
        getSelectBuilder().addAlias(tableInfo, alias);
    }

    public JoinStage(JoinType joinType, String joinTableStr, SelectBuilder selectBuilder, Consumer<ConditionExpressionBuilder> on) {
        this.joinType = joinType;
        setSelectBuilder(selectBuilder);
        getSelectBuilder().addJoinStages(this);
        this.on = on;

        this.tableInfo = new TableInfo(2, null, joinTableStr, null);
        // 注册表别名
        getSelectBuilder().addAlias(tableInfo);
    }

    public JoinStage(JoinType joinType, String joinTableStr, String alias, SelectBuilder selectBuilder, Consumer<ConditionExpressionBuilder> on) {
        this.joinType = joinType;
        setSelectBuilder(selectBuilder);
        getSelectBuilder().addJoinStages(this);
        this.on = on;

        this.tableInfo = new TableInfo(2, null, joinTableStr, null);
        // 注册表别名
        getSelectBuilder().addAlias(tableInfo, alias);
    }

    /**
     * JOIN 后可能是 WHERE 子句
     */
    public SelectWhereStage where(Consumer<ConditionExpressionBuilder> where) {
        return new SelectWhereStage(getSelectBuilder(), where);
    }

    /**
     * JOIN 后可能是 LEFT JOIN 子句
     */
    public JoinStage leftJoin(Class<?> joinTable, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.LEFT_JOIN, joinTable, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 LEFT JOIN 子句
     */
    public JoinStage leftJoin(Class<?> joinTable, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.LEFT_JOIN, joinTable, alias, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 LEFT JOIN 子句
     */
    public JoinStage leftJoin(String joinTableStr, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.LEFT_JOIN, joinTableStr, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 LEFT JOIN 子句
     */
    public JoinStage leftJoin(String joinTableStr, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.LEFT_JOIN, joinTableStr, alias, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 RIGHT JOIN 子句
     */
    public JoinStage rightJoin(Class<?> joinTable, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.RIGHT_JOIN, joinTable, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 RIGHT JOIN 子句
     */
    public JoinStage rightJoin(Class<?> joinTable, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.RIGHT_JOIN, joinTable, alias, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 RIGHT JOIN 子句
     */
    public JoinStage rightJoin(String joinTableStr, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.RIGHT_JOIN, joinTableStr, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 RIGHT JOIN 子句
     */
    public JoinStage rightJoin(String joinTableStr, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.RIGHT_JOIN, joinTableStr, alias, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 INNER JOIN 子句
     */
    public JoinStage innerJoin(Class<?> joinTable, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.INNER_JOIN, joinTable, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 INNER JOIN 子句
     */
    public JoinStage innerJoin(Class<?> joinTable, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.INNER_JOIN, joinTable, alias, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 INNER JOIN 子句
     */
    public JoinStage innerJoin(String joinTableStr, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.INNER_JOIN, joinTableStr, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 INNER JOIN 子句
     */
    public JoinStage innerJoin(String joinTableStr, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.INNER_JOIN, joinTableStr, alias, getSelectBuilder(), on);
    }

    /**
     * JOIN 后可能是 GROUP BY 子句
     */
    @SafeVarargs
    public final <T> GroupByStage groupBy(SFunction<T, ?>... groupColumns) {
        return new GroupByStage(getSelectBuilder(), Arrays.asList(groupColumns));
    }

    /**
     * JOIN 后可能是 GROUP BY 子句
     */
    public GroupByStage groupBy(Class<?> groupClass) {
        return new GroupByStage(getSelectBuilder(), groupClass);
    }

    /**
     * JOIN 后可能是 ORDER BY 子句
     */
    public <T> OrderByStage orderBy(SFunction<T, ?> orderColumn, OrderType orderType) {
        return new OrderByStage(getSelectBuilder(), new OrderItem(orderColumn, orderType));
    }

    /**
     * JOIN 后可能是 LIMIT 子句
     */
    public LimitStage limit(Integer limit) {
        return new LimitStage(getSelectBuilder(), limit);
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public String getJoinTable() {
        StringBuilder joinSql = new StringBuilder();
        String joinKey = getJoinType().getJoinKey();
        if (tableInfo.tableType() == 1) {
            Class<?> joinTable = tableInfo.tableCls();
            String joinTableName = Sql4jUtil.parseTableNameFromClass(joinTable);
            String joinTableAliasName = getSelectBuilder().getAlias(tableInfo);
            joinSql.append(joinKey).append(' ').append(joinTableName);
            if (joinTableAliasName != null) {
                joinSql.append(' ').append(joinTableAliasName);
            }
        } else if (tableInfo.tableType() == 2) {
            String joinTableAliasName = getSelectBuilder().getAlias(tableInfo);
            joinSql.append(joinKey).append(' ').append(tableInfo.tableStr());
            if (joinTableAliasName != null) {
                joinSql.append(' ').append(joinTableAliasName);
            }
        }
        return joinSql.toString();
    }

    public Consumer<ConditionExpressionBuilder> getOn() {
        return on;
    }

    @Override
    public SqlInfo end() {
        return getSelectBuilder().getTranslator().translate(getSelectBuilder());
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
