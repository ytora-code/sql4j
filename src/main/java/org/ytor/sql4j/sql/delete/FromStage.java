package org.ytor.sql4j.sql.delete;

import org.ytor.sql4j.sql.ConditionExpressionBuilder;
import org.ytor.sql4j.sql.SqlInfo;

import java.util.function.Consumer;

/**
 * FROM 阶段，指定要删除的目标表
 */
public class FromStage extends AbsDelete {

    private final Class<?> table;

    public FromStage(DeleteBuilder deleteBuilder, Class<?> table) {
        setDeleteBuilder(deleteBuilder);
        getDeleteBuilder().setFromStage(this);
        getDeleteBuilder().addAlias(table);
        this.table = table;
    }

    /**
     * FROM 后面一定是 WHERE 阶段
     */
    public DeleteWhereStage where(Consumer<ConditionExpressionBuilder> where) {
        return new DeleteWhereStage(getDeleteBuilder(), where);
    }

    /**
     * FROM 后可能结束
     */
    public SqlInfo end() {
        return getDeleteBuilder().getTranslator().translate(getDeleteBuilder());
    }

    public Class<?> getTable() {
        return table;
    }
}
