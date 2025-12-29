package xyz.ytora.sql4j.orm.autofill;

/**
 * 列的自动填充器
 */
public interface ColumnFiller {

    /**
     * 新增时自动填充
     */
    Object fillOnInsert();

    /**
     * 修改时自动填充
     */
    Object fillOnUpdate();
}
