package org.ytor.sql4j.sql.update;

import org.ytor.sql4j.sql.SFunction;

/**
 * UPDATE 阶段，指定要更新的表
 */
public class UpdateStage extends AbsUpdate {

    private final Class<?> table;

    public UpdateStage(UpdateBuilder updateBuilder, Class<?> table) {
        setUpdateBuilder(updateBuilder);
        updateBuilder.setUpdateStage(this);
        this.table = table;
    }

    /**
     * UPDATE 后面一定是 SET 阶段
     */
    public final SetStage set(SFunction<?, ?> updatedColumn, Object value) {
        return new SetStage(getUpdateBuilder()).set(updatedColumn, value);
    }

    public Class<?> getTable() {
        return table;
    }
}
