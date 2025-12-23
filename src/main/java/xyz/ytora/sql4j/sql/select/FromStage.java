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
     * 表类型：1-物理表(class实体类) / 2-物理表(字符串直接指定表名称) / 3-虚拟表（子查询）
     */
    private final Integer tableType;

    /**
     * FROM 主表
     */
    private Class<?> mainTable;

    /**
     * FROM 主表
     */
    private String mainTableStr;

    /**
     * FROM 子查询
     */
    private AbsSelect subSelect;

    public FromStage(SelectBuilder selectBuilder, Class<?> mainTable) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setFromBuilder(this);
        getSelectBuilder().addAlias(mainTable);
        this.mainTable = mainTable;
        tableType = 1;
    }

    public FromStage(SelectBuilder selectBuilder, String mainTableStr) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setFromBuilder(this);
        this.mainTableStr = mainTableStr;
        tableType = 2;
    }

    public FromStage(SelectBuilder selectBuilder, AbsSelect subSelect) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setFromBuilder(this);
        getSelectBuilder().addAlias(subSelect);
        this.subSelect = subSelect;
        tableType = 3;
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
        return tableType;
    }

    public String getFromTableSql(List<Object> orderedParms) {
        StringBuilder sql = new StringBuilder();
        if (tableType == 1) {
            String tableName = Sql4jUtil.parseTableNameFromClass(mainTable);
            sql.append(tableName).append(' ');
            String alias = getSelectBuilder().getAlias(mainTable);
            if (!getSelectBuilder().single()) {
                sql.append(alias).append(' ');
            }
            return sql.toString();
        } else if (tableType == 2) {
            return mainTableStr + ' ';
        }
        if (tableType == 3) {
            // 虚拟表
            AbsSelect subSelect = this.getSubSelect();
            sql.append('(');
            SqlInfo sqlInfo = subSelect.getSelectBuilder().getSQLHelper().getTranslator().translate(subSelect.getSelectBuilder());
            sql.append(sqlInfo.getSql());
            orderedParms.addAll(sqlInfo.getOrderedParms());
            sql.append(')').append(' ').append(getSelectBuilder().getAlias(subSelect)).append(' ');
        }
        throw new Sql4JException("未知的 FROM TABLE 类型: " + tableType);
    }

    public AbsSelect getSubSelect() {
        return subSelect;
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
