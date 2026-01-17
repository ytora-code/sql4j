package xyz.ytora.sql4j.meta.model;

/**
 * 序列元数据（只有部分数据库有）
 */
public class SequenceMeta {

    /**
     * 序列名称
     */
    private String name;

    /**
     * 所属 catalog
     */
    private String catalog;

    /**
     * 所属 schema
     */
    private String schema;

    /**
     * 序列注释
     */
    private String comment;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
