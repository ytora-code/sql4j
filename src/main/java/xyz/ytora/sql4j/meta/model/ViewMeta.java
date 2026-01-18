package xyz.ytora.sql4j.meta.model;

import java.util.List;

/**
 * 视图元数据
 */
public class ViewMeta {
    /**
     * 所属数据库
     */
    private String catalog;

    /**
     * 所属模式
     */
    private String schema;

    /**
     * 视图名称
     */
    private String name;

    /**
     * 视图注释
     */
    private String comment;

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

    public List<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    public void setColumnMetas(List<ColumnMeta> columnMetas) {
        this.columnMetas = columnMetas;
    }
}
