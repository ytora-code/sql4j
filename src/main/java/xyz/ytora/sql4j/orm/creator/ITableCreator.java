package xyz.ytora.sql4j.orm.creator;

import xyz.ytora.sql4j.enums.DbType;

import java.sql.Connection;

/**
 * 数据库表创建器
 */
public interface ITableCreator {

    /**
     * 返回支持的数据库类型
     */
    DbType getDbType();

    /**
     * 当前实体类在数据库中是否存在
     */
    <T> boolean exist(Connection connection, String tableName);

    /**
     * 根据实体类产生 DDL
     * @param clazz 实体类型
     * @param connection 连接对象
     */
    String toDDL(Class<?> clazz, Connection connection);

}
