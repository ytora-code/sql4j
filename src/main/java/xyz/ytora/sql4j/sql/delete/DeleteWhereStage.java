package xyz.ytora.sql4j.sql.delete;

import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.function.Consumer;

/**
 * DELETE 阶段，指定要删除的目标表
 */
public class DeleteWhereStage extends AbsDelete implements DeleteEndStage {

    /**
     * WHERE 表达式的构建规则
     */
    private Consumer<ConditionExpressionBuilder> whereExpr;

    /**
     * WHERE 表达式
     */
    private ConditionExpressionBuilder where;

    public DeleteWhereStage(DeleteBuilder deleteBuilder, Consumer<ConditionExpressionBuilder> whereExpr) {
        setDeleteBuilder(deleteBuilder);
        getDeleteBuilder().setWhereStage(this);
        this.whereExpr = whereExpr;
    }

    public DeleteWhereStage(DeleteBuilder deleteBuilder, ConditionExpressionBuilder where) {
        setDeleteBuilder(deleteBuilder);
        getDeleteBuilder().setWhereStage(this);
        this.where = where;
    }

    public ConditionExpressionBuilder getWhere() {
        if (where != null) {
            return where;
        }
        if (whereExpr != null) {
            ConditionExpressionBuilder expressionBuilder = new ConditionExpressionBuilder(getDeleteBuilder());
            whereExpr.accept(expressionBuilder);
            return expressionBuilder;
        }
        return null;
    }

    @Override
    public SqlInfo end() {
        return getDeleteBuilder().getTranslator().translate(getDeleteBuilder());
    }

    @Override
    public Integer submit() {
        return getDeleteBuilder().getSQLHelper().getSqlExecutionEngine().executeDelete(getDeleteBuilder().getTranslator().translate(getDeleteBuilder())).getEffectedRows();
    }
}
