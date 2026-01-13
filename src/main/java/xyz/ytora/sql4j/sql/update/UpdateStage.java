package xyz.ytora.sql4j.sql.update;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.select.TableInfo;

/**
 * UPDATE 阶段，指定要更新的表
 */
public class UpdateStage extends AbsUpdate {

    private final TableInfo tableInfo;

    public UpdateStage(UpdateBuilder updateBuilder, Class<?> table) {
        setUpdateBuilder(updateBuilder);
        getUpdateBuilder().setUpdateStage(this);
        this.tableInfo = new TableInfo(1, table, null, null);
        getUpdateBuilder().addAlias(tableInfo);
    }

    /**
     * UPDATE 后面一定是 SET 阶段
     */
    public final <T> SetStage set(SFunction<T, ?> updatedColumn, Object value) {
        return new SetStage(getUpdateBuilder()).set(updatedColumn, value);
    }

    public Class<?> getTable() {
        return tableInfo.tableCls();
    }
}
