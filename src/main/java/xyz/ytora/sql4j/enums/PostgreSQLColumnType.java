package xyz.ytora.sql4j.enums;

import xyz.ytora.ytool.json.JSON;

/**
 * 数据库表的列类型(postgresSQL)
 */
public enum PostgreSQLColumnType {
    INT1("SMALLINT")
    ,INT2("SMALLINT")
    ,INT3("SMALLINT")
    ,INT4("INTEGER")
    ,INT8("BIGINT")
    ,FLOAT("REAL")
    ,DOUBLE("DOUBLE PRECISION")
    ,BOOLEAN("BOOLEAN")
    ,VARCHAR255("VARCHAR(255)")
    ,VARCHAR64("VARCHAR(64)")
    ,VARCHAR16("VARCHAR(16)")
    ,BLOB("BYTEA")
    ,DATE("DATE")
    ,DATE_TIME("TIMESTAMP")
    ,TEXT("TEXT")
    ,JSON("JSONB")
    ;

    private final String columnTypeName;

    PostgreSQLColumnType(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }

    public static String getColumnTypeName(String columnTypeName) {
        for (PostgreSQLColumnType columnType : PostgreSQLColumnType.values()) {
            if (columnType.name().equals(columnTypeName)) {
                return columnType.columnTypeName;
            }
        }
        // 默认使用 TEXT 类型
        return TEXT.columnTypeName;
    }
}
