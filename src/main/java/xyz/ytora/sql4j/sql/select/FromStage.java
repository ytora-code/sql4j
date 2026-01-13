package xyz.ytora.sql4j.sql.select;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.enums.JoinType;
import xyz.ytora.sql4j.enums.OrderType;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.OrderItem;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.util.Sql4jUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * FROM 阶段
 */
public class FromStage extends AbsSelect implements SelectEndStage {

    /**
     * FROM 主表
     */
    private final TableInfo tableInfo;

    public FromStage(SelectBuilder selectBuilder, Class<?> mainTable) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setFromBuilder(this);
        this.tableInfo = new TableInfo(1, mainTable, null, null);
        getSelectBuilder().addAlias(this.tableInfo);
    }

    public FromStage(SelectBuilder selectBuilder, Class<?> mainTable, String alias) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setFromBuilder(this);
        this.tableInfo = new TableInfo(1, mainTable, null, null);
        getSelectBuilder().addAlias(this.tableInfo, alias);
    }

    public FromStage(SelectBuilder selectBuilder, String mainTableStr) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setFromBuilder(this);
        this.tableInfo = new TableInfo(2, null, mainTableStr, null);
        getSelectBuilder().addAlias(this.tableInfo);
    }

    public FromStage(SelectBuilder selectBuilder, String mainTableStr, String alias) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setFromBuilder(this);
        this.tableInfo = new TableInfo(2, null, mainTableStr, null);
        getSelectBuilder().addAlias(this.tableInfo, alias);
    }

    public FromStage(SelectBuilder selectBuilder, AbsSelect subSelect) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setFromBuilder(this);
        this.tableInfo = new TableInfo(3, null, null, subSelect);
        getSelectBuilder().addAlias(this.tableInfo);
    }

    public FromStage(SelectBuilder selectBuilder, AbsSelect subSelect, String alias) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setFromBuilder(this);
        this.tableInfo = new TableInfo(3, null, null, subSelect);
        getSelectBuilder().addAlias(this.tableInfo, alias);
    }

    /**
     * FROM 后可能是 LEFT JOIN 子句
     */
    public JoinStage leftJoin(Class<?> joinTable, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.LEFT_JOIN, joinTable, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 LEFT JOIN 子句
     */
    public JoinStage leftJoin(Class<?> joinTable, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.LEFT_JOIN, joinTable, alias, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 LEFT JOIN 子句
     */
    public JoinStage leftJoin(String joinTableStr, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.LEFT_JOIN, joinTableStr, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 LEFT JOIN 子句
     */
    public JoinStage leftJoin(String joinTableStr, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.LEFT_JOIN, joinTableStr, alias, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 RIGHT JOIN 子句
     */
    public JoinStage rightJoin(Class<?> joinTable, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.RIGHT_JOIN, joinTable, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 RIGHT JOIN 子句
     */
    public JoinStage rightJoin(Class<?> joinTable, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.RIGHT_JOIN, joinTable, alias, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 RIGHT JOIN 子句
     */
    public JoinStage rightJoin(String joinTableStr, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.RIGHT_JOIN, joinTableStr, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 RIGHT JOIN 子句
     */
    public JoinStage rightJoin(String joinTableStr, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.RIGHT_JOIN, joinTableStr, alias, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 INNER JOIN 子句
     */
    public JoinStage innerJoin(Class<?> joinTable, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.INNER_JOIN, joinTable, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 INNER JOIN 子句
     */
    public JoinStage innerJoin(Class<?> joinTable, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.INNER_JOIN, joinTable, alias, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 INNER JOIN 子句
     */
    public JoinStage innerJoin(String joinTableStr, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.INNER_JOIN, joinTableStr, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 INNER JOIN 子句
     */
    public JoinStage innerJoin(String joinTableStr, String alias, Consumer<ConditionExpressionBuilder> on) {
        return new JoinStage(JoinType.INNER_JOIN, joinTableStr, alias, getSelectBuilder(), on);
    }

    /**
     * FROM 后可能是 WHERE 子句
     */
    public SelectWhereStage where(Consumer<ConditionExpressionBuilder> where) {
        return new SelectWhereStage(getSelectBuilder(), where);
    }

    /**
     * FROM 后可能是 WHERE 子句
     */
    public SelectWhereStage where(ConditionExpressionBuilder whereExpr) {
        return new SelectWhereStage(getSelectBuilder(), whereExpr);
    }

    /**
     * WHERE 后可能是 GROUP BY 子句
     */
    public <T> GroupByStage groupBy(SFunction<T, ?> groupColumn) {
        return new GroupByStage(getSelectBuilder(), groupColumn);
    }

    /**
     * WHERE 后可能是 GROUP BY 子句
     */
    public GroupByStage groupBy(Class<?> groupClass) {
        return new GroupByStage(getSelectBuilder(), groupClass);
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

    public Integer getTableType() {
        return tableInfo.tableType();
    }

    public String getFromTableSql(List<Object> orderedParms) {
        StringBuilder sql = new StringBuilder();
        // 实体类表
        if (tableInfo.tableType() == 1) {
            Class<?> mainTable = tableInfo.tableCls();
            String tableName = Sql4jUtil.parseTableNameFromClass(mainTable);
            sql.append(tableName).append(' ');
            if (!getSelectBuilder().single()) {
                String alias = getSelectBuilder().getAlias(tableInfo);
                sql.append(alias).append(' ');
            }
            return sql.toString();
        }
        // 字符串表
        else if (tableInfo.tableType() == 2) {
            sql.append(tableInfo.tableStr()).append(' ');
            if (!getSelectBuilder().single()) {
                String alias = getSelectBuilder().getAlias(tableInfo);
                sql.append(alias).append(' ');
            }
            return sql.toString();
        }
        // 子查询虚拟表
        else if (tableInfo.tableType() == 3) {
            // 虚拟表
            AbsSelect subSelect = tableInfo.subSelect();
            sql.append('(');
            SqlInfo sqlInfo = subSelect.getSelectBuilder().getSQLHelper().getTranslator().translate(subSelect.getSelectBuilder());
            sql.append(sqlInfo.getSql());
            orderedParms.addAll(sqlInfo.getOrderedParms());
            sql.append(')').append(' ').append(getSelectBuilder().getAlias(tableInfo)).append(' ');
            return sql.toString();
        }
        throw new Sql4JException("未知的 FROM TABLE 类型: " + tableInfo.tableType());
    }

    public AbsSelect getSubSelect() {
        return tableInfo.subSelect();
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
