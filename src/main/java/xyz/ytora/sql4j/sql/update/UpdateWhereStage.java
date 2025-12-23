package xyz.ytora.sql4j.sql.update;

import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.function.Consumer;

/**
 * WHERE 阶段，指定更新条件
 */
public class UpdateWhereStage extends AbsUpdate implements UpdateEndStage {

    /**
     * WHERE 表达式的构建规则
     */
    private Consumer<ConditionExpressionBuilder> whereExpr;

    private ConditionExpressionBuilder where;

    public UpdateWhereStage(UpdateBuilder updateBuilder, Consumer<ConditionExpressionBuilder> whereExpr) {
        setUpdateBuilder(updateBuilder);
        getUpdateBuilder().setWhereStage(this);
        this.whereExpr = whereExpr;
    }

    public UpdateWhereStage(UpdateBuilder updateBuilder, ConditionExpressionBuilder where) {
        setUpdateBuilder(updateBuilder);
        getUpdateBuilder().setWhereStage(this);
        this.where = where;
    }

    public ConditionExpressionBuilder getWhere() {
        if (where != null) {
            return where;
        }
        if (whereExpr != null) {
            ConditionExpressionBuilder expressionBuilder = new ConditionExpressionBuilder(getUpdateBuilder());
            whereExpr.accept(expressionBuilder);
            return expressionBuilder;
        }
        return null;
    }

    @Override
    public SqlInfo end() {
        return getUpdateBuilder().getTranslator().translate(getUpdateBuilder());
    }

    @Override
    public Integer submit() {
        return getUpdateBuilder().getSQLHelper().getSqlExecutionEngine().executeUpdate(getUpdateBuilder().getTranslator().translate(getUpdateBuilder())).getEffectedRows();
    }
}
