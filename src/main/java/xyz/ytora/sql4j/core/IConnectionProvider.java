package xyz.ytora.sql4j.core;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接
 */
public interface IConnectionProvider {

    /**
     * 获取数据库连接
     */
    Connection getConnection() throws SQLException;

    /**
     * 关闭数据库连接
     */
    void closeConnection(Connection connection) throws SQLException;

}
