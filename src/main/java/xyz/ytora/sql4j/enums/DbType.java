package xyz.ytora.sql4j.enums;

/**
 * 数据库类型
 */
public enum DbType {

    MYSQL("MySQL"),
    MARIADB("MariaDB"),
    POSTGRESQL("PostgreSQL"),
    ORACLE("Oracle"),
    SQLSERVER("Microsoft SQL Server"),
    SQLite("SQLite"),
    DB2("DB2"),
    H2("H2"),
    DERBY("Apache Derby"),
    SYBASE("Sybase SQL Server"),
    INFORMIX("Informix Dynamic Server");

    DbType(String productName) {
        this.productName = productName;
    }

    private final String productName;

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

        for (DbType dbType : DbType.values()) {
            if (dbType.productName.equalsIgnoreCase(databaseProductName)) {
                return dbType;
            }
        }
        throw new IllegalArgumentException("未知的数据库: " + databaseProductName);
    }
}
