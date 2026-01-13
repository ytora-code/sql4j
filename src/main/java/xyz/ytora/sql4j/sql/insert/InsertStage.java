package xyz.ytora.sql4j.sql.insert;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.select.TableInfo;

import java.util.Arrays;
import java.util.Collection;

/**
 * INSERT 阶段，指定要插入的表
 */
public class InsertStage extends AbsInsert {

    private final TableInfo tableInfo;

    public InsertStage(InsertBuilder insertBuilder, Class<?> table) {
        setInsertBuilder(insertBuilder);
        getInsertBuilder().setInsertStage(this);
        this.tableInfo = new TableInfo(1, table, null, null);
        getInsertBuilder().addAlias(tableInfo);
    }

    /**
     * INSERT 后面一定是 INTO
     */
    @SafeVarargs
    public final <T> IntoStage into(SFunction<T, ?>... insertedColumn) {
        return new IntoStage(getInsertBuilder(), Arrays.asList(insertedColumn));
    }

    /**
     * INSERT 后面一定是 INTO
     */
    public final <T> IntoStage into(Collection<SFunction<T, ?>> insertedColumn) {
        return new IntoStage(getInsertBuilder(), insertedColumn);
    }

    public Class<?> getTable() {
        return tableInfo.tableCls();
    }
}
