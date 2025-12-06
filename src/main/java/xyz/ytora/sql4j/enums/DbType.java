package xyz.ytora.sql4j.enums;

/**
 * 数据库类型
 */
public enum DbType {

    MYSQL, MariaDB, PG, ORACLE, SQL_SERVER, SQLite;

    /**
     * 根据数据库产品名称获取对应的枚举值
     *
     * @param databaseProductName 数据库产品名称，例如：MySQL、PostgreSQL、Oracle、SQL Server
     * @return 对应的数据库类型枚举
     */
    public static DbType fromString(String databaseProductName) {
        if (databaseProductName == null) {
            throw new IllegalArgumentException("Database product name cannot be null");
        }

        // 根据数据库产品名称返回对应的枚举
        return switch (databaseProductName.toLowerCase()) {
            case "mysql" -> MYSQL;
            case "postgresql", "pg" -> PG;
            case "oracle" -> ORACLE;
            case "microsoft sql server", "sql server" -> SQL_SERVER;
            default -> throw new IllegalArgumentException("未知的数据库: " + databaseProductName);
        };
    }
}
