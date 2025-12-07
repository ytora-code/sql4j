package xyz.ytora.sql4j.meta.model;

/**
 * 列元数据
 */
public class ColumnMeta {
    /**
     * 所属数据库
     */
    private String catalog;

    /**
     * 所属模式
     */
    private String schema;

    /**
     * 所属表
     */
    private String table;

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 是否属于主键
     */
    private Boolean isPrimaryKey;

    /**
     * 是否允许为空
     */
    private Boolean nullable;

    /**
     * 是否自增
     */
    private Boolean autoIncrement;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 字段类型
     */
    private String columnType;

    /**
     * 字段小数点长度
     */
    private Integer decimalDigits;

    /**
     * 字段对应Java类型
     */
    private String javaType;

    /**
     * 字段对应ts类型
     */
    private String tsType;

    /**
     * 字段长度
     */
    private Integer columnLength;

    /**
     * 字段注释
     */
    private String columnComment;

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

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Boolean getPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getTsType() {
        return tsType;
    }

    public void setTsType(String tsType) {
        this.tsType = tsType;
    }

    public Integer getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(Integer columnLength) {
        this.columnLength = columnLength;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}
