package xyz.ytora.sql4j.meta.model;

public class ForeignKeyColumnMeta {

    /**
     * 外键字段名（当前表）
     */
    private String fkColumn;

    /**
     * 主键字段名（被引用表）
     */
    private String pkColumn;

    /**
     * 列顺序（多列外键）
     */
    private short seq;

    public String getFkColumn() {
        return fkColumn;
    }

    public void setFkColumn(String fkColumn) {
        this.fkColumn = fkColumn;
    }

    public String getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(String pkColumn) {
        this.pkColumn = pkColumn;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }
}
