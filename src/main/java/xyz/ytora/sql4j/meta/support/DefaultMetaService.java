package xyz.ytora.sql4j.meta.support;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.core.IConnectionProvider;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.enums.DbType;
import xyz.ytora.sql4j.meta.IMetaService;
import xyz.ytora.sql4j.meta.model.ColumnMeta;
import xyz.ytora.sql4j.meta.model.TableMeta;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取表的元数据
 */
public class DefaultMetaService implements IMetaService {

    IConnectionProvider connectionProvider;

    public DefaultMetaService(IConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public DbType inferDbType() throws SQLException {
        return inferDbType(connectionProvider.getConnection());
    }

    @Override
    public DbType inferDbType(Connection connection) throws SQLException {
        check();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            // 数据库产品名称
            String productName = metaData.getDatabaseProductName();
            // 获取数据库类型枚举
            return DbType.fromString(productName);
        } finally {
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
    }

    @Override
    public List<String> listCatalogs() throws SQLException {
        List<String> list = new ArrayList<>();
        Connection connection = null;
        ResultSet rs = null;

        try {
            connection = connectionProvider.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            rs = metaData.getCatalogs();
            while (rs.next()) {
                String catalogName = rs.getString("TABLE_CAT");
                list.add(catalogName);
            }
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }

    @Override
    public List<String> listSchemas(String catalog) throws SQLException {
        List<String> list = new ArrayList<>();
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = connectionProvider.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            rs = metaData.getSchemas(catalog, null);
            while (rs.next()) {
                String schemaName = rs.getString("TABLE_SCHEM");
                list.add(schemaName);
            }
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }

    @Override
    public List<TableMeta> listTables(String catalog, String schema, String table) throws SQLException {
        List<TableMeta> list = new ArrayList<>();
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = connectionProvider.getConnection();
            catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            rs = metaData.getTables(catalog, schema, table, new String[]{"TABLE"});
            while (rs.next()) {
                TableMeta tableMeta = new TableMeta();
                //获取表名
                String tableName = rs.getString("TABLE_NAME");
                tableMeta.setTable(tableName);
                //表描述
                String remarks = rs.getString("REMARKS");
                tableMeta.setComment(remarks);
                tableMeta.setCatalog(catalog);
                tableMeta.setSchema(schema);

                //获取表主键
                List<String> keys = parsePrimaryKeys(metaData, catalog, schema, tableName);
                tableMeta.setPrimaryKeys(keys);
                list.add(tableMeta);
            }
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }

    @Override
    public List<ColumnMeta> listColumns(String catalog, String schema, String tableName) throws SQLException {
        List<ColumnMeta> list = new ArrayList<>();
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = connectionProvider.getConnection();
            catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            rs = metaData.getColumns(catalog, schema, tableName, null);
            while (rs.next()) {
                ColumnMeta columnMeta = new ColumnMeta();
                columnMeta.setCatalog(catalog);
                columnMeta.setSchema(schema);
                columnMeta.setTable(tableName);
                //列名
                String columnName = rs.getString("COLUMN_NAME");
                columnMeta.setColumnName(columnName);
                //字段类型
                String typeName = rs.getString("TYPE_NAME");
                columnMeta.setColumnType(typeName);
                //java类型
                String javaType = rs.getString("DATA_TYPE");
                columnMeta.setJavaType(javaType);
                //是否自增
                boolean isAutoincrement = rs.getBoolean("IS_AUTOINCREMENT");
                columnMeta.setAutoIncrement(isAutoincrement);
                //字段长度
                int columnSize = rs.getInt("COLUMN_SIZE");
                columnMeta.setColumnLength(columnSize);
                //小数点位数
                int decimalDigits = rs.getInt("DECIMAL_DIGITS");
                columnMeta.setDecimalDigits(decimalDigits);
                //字段是否允许为空
                boolean isNullable = (rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                columnMeta.setNullable(isNullable);
                //字段备注
                String columnComment = rs.getString("REMARKS");
                columnMeta.setColumnComment(columnComment);
                //默认值
                String defaultValue = rs.getString("COLUMN_DEF");
                columnMeta.setDefaultValue(defaultValue);

                list.add(columnMeta);
            }
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }

        return list;
    }

    @Override
    public List<String> listPrimaryKeys(String catalog, String schema, String tableName) throws SQLException {
        List<String> list;
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            list = parsePrimaryKeys(metaData, catalog, schema, tableName);

        } finally {
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }

    private void check() {
        if (connectionProvider == null) {
            throw new Sql4JException("connectionProvider 尚未准备好");
        }
    }

    private List<String> parsePrimaryKeys(DatabaseMetaData metaData, String catalogsName, String schema, String tableName) throws SQLException {
        List<String> keys = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = metaData.getPrimaryKeys(catalogsName, schema, tableName);

            while (rs.next()) {
                String keyName = rs.getString("COLUMN_NAME");
                keys.add(keyName);
            }
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        return keys;
    }
}
