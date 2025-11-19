package org.ytor.sql4j.sql.update;

import org.ytor.sql4j.sql.ConditionExpressionBuilder;
import org.ytor.sql4j.sql.SqlInfo;

import java.util.function.Consumer;

/**
 * WHERE 阶段，指定更新条件
 */
public class UpdateWhereStage extends AbsUpdate implements UpdateEndStage {

    /**
     * WHERE 表达式的构建规则
     */
    private final Consumer<ConditionExpressionBuilder> where;

    /**
     * WHERE 表达式
     */
    private ConditionExpressionBuilder whereExpression;

    public UpdateWhereStage(UpdateBuilder updateBuilder, Consumer<ConditionExpressionBuilder> where) {
        setUpdateBuilder(updateBuilder);
        getUpdateBuilder().setWhereStage(this);
        this.where = where;
    }

    /**
     * WHERE 后可能结束
     */
    public SqlInfo end() {
        return getUpdateBuilder().getTranslator().translate(getUpdateBuilder());
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
        return getUpdateBuilder().getSQLHelper().getSqlExecutionEngine().executeUpdate(getUpdateBuilder().getTranslator().translate(getUpdateBuilder())).getEffectedRows();
    }
}
