package xyz.ytora.sql4j.enums;

/**
 * 数据库类型
 */
public enum DatabaseType {

    MYSQL, PG, ORACLE, SQL_SERVER;

    /**
     * 根据数据库产品名称获取对应的枚举值
     *
     * @param databaseProductName 数据库产品名称，例如：MySQL、PostgreSQL、Oracle、SQL Server
     * @return 对应的数据库类型枚举
     */
    public static DatabaseType fromString(String databaseProductName) {
        if (databaseProductName == null) {
            throw new IllegalArgumentException("Database product name cannot be null");
        }

        // 根据数据库产品名称返回对应的枚举
        switch (databaseProductName.toLowerCase()) {
            case "mysql":
                return MYSQL;
            case "postgresql":
            case "pg":
                return PG;
            case "oracle":
                return ORACLE;
            case "microsoft sql server":
            case "sql server":
                return SQL_SERVER;
            default:
                throw new IllegalArgumentException("Unknown database: " + databaseProductName);
        }
    }
}
