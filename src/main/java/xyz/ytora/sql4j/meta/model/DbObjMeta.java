package xyz.ytora.sql4j.meta.model;

import xyz.ytora.sql4j.enums.DbObjType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 数据库对象元数据
 */
public class DbObjMeta {

    /**
     * 对象名称
     */
    private String name;

    /**
     * 对象类型
     */
    private DbObjType type;

    /**
     * 所属 catalog（MySQL = database）
     */
    private String catalog;

    /**
     * 所属 schema（MySQL 中为虚拟 schema）
     */
    private String schema;

    /**
     * 对象注释 / 描述
     */
    private String comment;

    /**
     * 是否系统对象
     */
    private Boolean system;

    /**
     * 创建时间（如果数据库支持）
     */
    private LocalDateTime createTime;

    /**
     * 最后修改时间（如果数据库支持）
     */
    private LocalDateTime updateTime;

    /**
     * 扩展属性（数据库方言字段）
     */
    private Map<String, Object> attributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DbObjType getType() {
        return type;
    }

    public void setType(DbObjType type) {
        this.type = type;
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

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
