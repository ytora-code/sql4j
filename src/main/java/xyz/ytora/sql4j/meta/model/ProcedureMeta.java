package xyz.ytora.sql4j.meta.model;

/**
 * 存储过程元数据
 */
public class ProcedureMeta {

    /**
     * 存储过程名称
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
     * 存储过程注释
     */
    private String comment;

    /**
     * 存储过程类型
     */
    private short procedureType;

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

    public short getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(short procedureType) {
        this.procedureType = procedureType;
    }
}
