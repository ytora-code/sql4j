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
    private String name;

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

    /**
     * 外键元数据
     */
    private List<ForeignKeyMeta> foreignKeyMetas;

    /**
     * 索引元数据
     */
    private List<IndexMeta> indexMetas;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<ForeignKeyMeta> getForeignKeyMetas() {
        return foreignKeyMetas;
    }

    public void setForeignKeyMetas(List<ForeignKeyMeta> foreignKeyMetas) {
        this.foreignKeyMetas = foreignKeyMetas;
    }

    public List<IndexMeta> getIndexMetas() {
        return indexMetas;
    }

    public void setIndexMetas(List<IndexMeta> indexMetas) {
        this.indexMetas = indexMetas;
    }
}
