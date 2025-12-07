package xyz.ytora.sql4j.meta;

import xyz.ytora.sql4j.enums.DbType;
import xyz.ytora.sql4j.meta.model.ColumnMeta;
import xyz.ytora.sql4j.meta.model.TableMeta;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 获取表的元数据
 */
public interface IMetaService {

    /**
     * 推断数据库类型
     */
    DbType inferDbType() throws SQLException;

    /**
     * 推断数据库类型
     */
    DbType inferDbType(Connection connection) throws SQLException;

    /**
     * 获取所有数据库名称，MySQL中 catalog 和 schema 是等价的
     */
    List<String> listCatalogs() throws SQLException;

    /**
     * 获取指定库下的所有模式名称，MySQL调用该方法获取的是空数组
     */
    List<String> listSchemas(String catalog) throws SQLException;

    /**
     * 获取指定库的指定模式下面所有表名称
     */
    List<TableMeta> listTables(String catalog, String schema, String table) throws SQLException;

    /**
     * 获取指定表的所有列消息
     */
    List<ColumnMeta> listColumns(String catalog, String schema, String tableName) throws SQLException;

    /**
     * 获取指定表的所有主键
     */
    List<String> listPrimaryKeys(String catalog, String schema, String tableName) throws SQLException;
}
