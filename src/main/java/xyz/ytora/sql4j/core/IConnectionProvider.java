package xyz.ytora.sql4j.core;

import java.sql.Connection;

/**
 * 数据库连接
 */
public interface IConnectionProvider {

    /**
     * 获取数据库连接
     */
    Connection getConnection();

    /**
     * 关闭数据库连接
     */
    void closeConnection(Connection connection);

}
