package xyz.ytora.sql4j.enums;

/**
 * 数据库表的列类型 (MySQL)
 */
public enum MySQLColumnType {
    INT1("TINYINT")  // 1字节整数
    ,INT2("SMALLINT") // 2字节整数
    ,INT3("MEDIUMINT") // 3字节整数
    ,INT4("INT") // 4字节整数
    ,INT8("BIGINT") // 8字节整数
    ,FLOAT("FLOAT") // 单精度浮点数
    ,DOUBLE("DOUBLE") // 双精度浮点数
    ,BOOLEAN("BOOLEAN") // 布尔类型 (MySQL使用 TINYINT(1) 来表示)
    ,VARCHAR255("VARCHAR(255)") // 字符串类型，最大255个字符
    ,VARCHAR64("VARCHAR(64)") // 字符串类型，最大64个字符
    ,VARCHAR16("VARCHAR(16)") // 字符串类型，最大16个字符
    ,BLOB("BLOB") // 二进制大对象 (用于存储二进制数据)
    ,DATE("DATE") // 日期类型
    ,DATE_TIME("DATETIME") // 日期时间类型
    ,TEXT("TEXT") // 文本类型
    ,JSON("JSON") // JSON类型
    ;

    private final String columnTypeName;

    MySQLColumnType(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }

    public static String getColumnTypeName(String columnTypeName) {
        for (MySQLColumnType columnType : MySQLColumnType.values()) {
            if (columnType.name().equals(columnTypeName)) {
                return columnType.columnTypeName;
            }
        }
        // 默认使用 TEXT 类型
        return TEXT.columnTypeName;
    }
}
