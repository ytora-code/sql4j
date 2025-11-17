package org.ytor.sql4j.sql.update;

import org.ytor.sql4j.func.SFunction;

/**
 * UPDATE 阶段，指定要更新的表
 */
public class UpdateStage extends AbsUpdate {

    private final Class<?> table;

    public UpdateStage(UpdateBuilder updateBuilder, Class<?> table) {
        setUpdateBuilder(updateBuilder);
        getUpdateBuilder().setUpdateStage(this);
        getUpdateBuilder().addAlias(table);
        this.table = table;
    }

    /**
     * UPDATE 后面一定是 SET 阶段
     */
    public final <T> SetStage set(SFunction<T, ?> updatedColumn, Object value) {
        return new SetStage(getUpdateBuilder()).set(updatedColumn, value);
    }

    public Class<?> getTable() {
        return table;
    }
}
