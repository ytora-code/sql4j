package xyz.ytora.sql4j.sql.select;

/**
 * 封装了 SELECT 查询中涉及的表
 */
public final class TableInfo {
    /**
     * 表类型：1-物理表(class实体类) / 2-物理表(字符串直接指定表名称) / 3-虚拟表（子查询）
     */
    private final Integer tableType;

    /**
     * 实体性形式的表
     */
    private final Class<?> tableCls;

    /**
     * 字符串形式的表
     */
    private final String tableStr;

    /**
     * 子查询形式的表
     */
    private final AbsSelect subSelect;

    public TableInfo(Integer tableType, Class<?> tableCls, String tableStr, AbsSelect subSelect) {
        this.tableType = tableType;
        this.tableCls = tableCls;
        this.tableStr = tableStr;
        this.subSelect = subSelect;
    }

    public Integer tableType() {
        return tableType;
    }

    public Class<?> tableCls() {
        return tableCls;
    }

    public String tableStr() {
        return tableStr;
    }

    public AbsSelect subSelect() {
        return subSelect;
    }
}
