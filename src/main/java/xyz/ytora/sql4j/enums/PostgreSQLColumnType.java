package xyz.ytora.sql4j.enums;

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

    public static boolean isStr(PostgreSQLColumnType type) {
        return switch (type) {
            case VARCHAR255, VARCHAR64, VARCHAR16, TEXT, JSON, DATE, DATE_TIME, BLOB -> true;
            default -> false;
        };
    }

    public static boolean isStr(String columnTypeName) {
        PostgreSQLColumnType type;
        try {
            type = PostgreSQLColumnType.valueOf(columnTypeName);
        } catch (IllegalArgumentException e) {
            // 未识别类型，按字符串处理更安全
            return true;
        }
        return isStr(type);
    }

}
