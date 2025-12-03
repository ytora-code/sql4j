package xyz.ytora.sql4j.sql.insert;

import xyz.ytora.sql4j.func.SFunction;

import java.util.Arrays;

/**
 * INSERT 阶段，指定要插入的表
 */
public class InsertStage extends AbsInsert {

    private final Class<?> table;

    public InsertStage(InsertBuilder insertBuilder, Class<?> table) {
        setInsertBuilder(insertBuilder);
        getInsertBuilder().setInsertStage(this);
        getInsertBuilder().addAlias(table);
        this.table = table;
    }

    /**
     * INSERT 后面一定是 INTO
     */
    @SafeVarargs
    public final <T> IntoStage into(SFunction<T, ?>... insertedColumn) {
        return new IntoStage(getInsertBuilder(), Arrays.asList(insertedColumn));
    }

    public Class<?> getTable() {
        return table;
    }
}
