package xyz.ytora.sql4j.meta.model;

import java.util.List;

/**
 * 表元数据
 */
public class TableMeta {
    /**
     * 所属数据库
     */
    private String catalog;

    /**
     * 所属模式
     */
    private String schema;

    /**
     * 表名称
     */
    private String table;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 主键字段
     */
    private List<String> primaryKeys;

    /**
     * 列元数据
     */
    private List<ColumnMeta> columnMetas;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public List<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    public void setColumnMetas(List<ColumnMeta> columnMetas) {
        this.columnMetas = columnMetas;
    }
}
