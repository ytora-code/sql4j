package xyz.ytora.sql4j.meta.model;

/**
 * 函数元数据
 */
public class FunctionMeta {

    /**
     * 函数名称
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
     * 函数注释
     */
    private String comment;

    /**
     * 函数返回结果
     */
    private short returnType;

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

    public short getReturnType() {
        return returnType;
    }

    public void setReturnType(short returnType) {
        this.returnType = returnType;
    }
}
