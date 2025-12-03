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
    private final Consumer<ConditionExpressionBuilder> where;

    /**
     * WHERE 表达式
     */
    private ConditionExpressionBuilder whereExpression;

    public DeleteWhereStage(DeleteBuilder deleteBuilder, Consumer<ConditionExpressionBuilder> where) {
        setDeleteBuilder(deleteBuilder);
        getDeleteBuilder().setWhereStage(this);
        this.where = where;
    }

    /**
     * WHERE 后可能结束
     */
    public SqlInfo end() {
        return getDeleteBuilder().getTranslator().translate(getDeleteBuilder());
    }

    public Consumer<ConditionExpressionBuilder> getWhere() {
        return where;
    }

    public ConditionExpressionBuilder getWhereExpression() {
        return whereExpression;
    }

    public void setWhereExpression(ConditionExpressionBuilder whereExpression) {
        this.whereExpression = whereExpression;
    }

    @Override
    public Integer submit() {
        return getDeleteBuilder().getSQLHelper().getSqlExecutionEngine().executeDelete(getDeleteBuilder().getTranslator().translate(getDeleteBuilder())).getEffectedRows();
    }
}
