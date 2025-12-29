package xyz.ytora.sql4j.orm.autofill;

/**
 * 列的自动填充
 */
public class ColumnFillerAdapter implements ColumnFiller {

    /**
     * 新增时自动填充
     */
    public Object fillOnInsert() {
        return null;
    }

    /**
     * 修改时自动填充
     */
    public Object fillOnUpdate() {
        return null;
    }
}
