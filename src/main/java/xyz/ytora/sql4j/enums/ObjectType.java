package xyz.ytora.sql4j.enums;

/**
 * 数据库对象类型
 */
public enum ObjectType {
    
    // 表：用来存储数据的基础单位
    TABLE("Table"),
    
    // 视图：虚拟表，通常是基于查询的结果集
    VIEW("View"),
    
    // 索引：提高查询性能的数据库对象
    INDEX("Index"),
    
    // 函数：可以在 SQL 语句中调用的可执行代码块
    FUNCTION("Function"),
    
    // 存储过程：封装的 SQL 操作，可以执行复杂的数据库操作
    PROCEDURE("Procedure"),
    
    // 触发器：在特定数据库操作（如 INSERT、UPDATE 或 DELETE）发生时自动执行的操作
    TRIGGER("Trigger"),
    
    // 序列：生成唯一的数字，通常用于主键
    SEQUENCE("Sequence"),
    
    // 约束：限制数据输入或表关系，如主键、外键等
    CONSTRAINT("Constraint"),
    
    // 视图：查询结果集的抽象表示（如果需要和视图区别，已经用上面声明了）
    SYNONYM("Synonym");
    
    private final String typeName;
    
    // 构造函数
    ObjectType(String typeName) {
        this.typeName = typeName;
    }
    
    // 获取数据库对象类型名称
    public String getTypeName() {
        return typeName;
    }
    
    @Override
    public String toString() {
        return this.typeName;
    }
}
