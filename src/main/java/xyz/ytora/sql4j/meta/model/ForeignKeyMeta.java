package xyz.ytora.sql4j.meta.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 外键元数据
 */
public class ForeignKeyMeta {

    /**
     * 外键名称
     */
    private String name;

    /**
     * pkTable
     */
    private String pkTable;

    /**
     * fkTable
     */
    private String fkTable;

    /**
     * deleteRule
     */
    private short deleteRule;

    /**
     * updateRule
     */
    private short updateRule;

    /**
     * 外键列映射（多列）
     */
    private List<ForeignKeyColumnMeta> columns = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPkTable() {
        return pkTable;
    }

    public void setPkTable(String pkTable) {
        this.pkTable = pkTable;
    }

    public String getFkTable() {
        return fkTable;
    }

    public void setFkTable(String fkTable) {
        this.fkTable = fkTable;
    }

    public short getDeleteRule() {
        return deleteRule;
    }

    public void setDeleteRule(short deleteRule) {
        this.deleteRule = deleteRule;
    }

    public short getUpdateRule() {
        return updateRule;
    }

    public void setUpdateRule(short updateRule) {
        this.updateRule = updateRule;
    }

    public void addColumn(short seq, String fkColumn, String pkColumn) {
        ForeignKeyColumnMeta c = new ForeignKeyColumnMeta();
        c.setSeq(seq);
        c.setFkColumn(fkColumn);
        c.setPkColumn(pkColumn);
        this.columns.add(c);
    }
}
