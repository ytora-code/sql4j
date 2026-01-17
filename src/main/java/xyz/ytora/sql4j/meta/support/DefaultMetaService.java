package xyz.ytora.sql4j.meta.support;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.core.IConnectionProvider;
import xyz.ytora.sql4j.enums.DbType;
import xyz.ytora.sql4j.meta.IMetaService;
import xyz.ytora.sql4j.meta.model.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取表的元数据
 */
public class DefaultMetaService implements IMetaService {

    IConnectionProvider connectionProvider;

    public DefaultMetaService(IConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public DbType inferDbType() {
        return inferDbType(connectionProvider.getConnection());
    }

    @Override
    public DbType inferDbType(Connection connection) {
        check();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            // 数据库产品名称
            String productName = metaData.getDatabaseProductName();
            // 获取数据库类型枚举
            return DbType.fromString(productName);
        } catch (SQLException e) {
            throw new Sql4JException(e);
        } finally {
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
    }

    @Override
    public List<String> listCatalogs() {
        List<String> list = new ArrayList<>();
        Connection connection = null;

        try {
            connection = connectionProvider.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getCatalogs();) {
                while (rs.next()) {
                    String catalogName = rs.getString("TABLE_CAT");
                    list.add(catalogName);
                }
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        } finally {
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }

    @Override
    public List<String> listSchemas(String catalog) {
        List<String> list = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getSchemas(catalog, null)) {
                while (rs.next()) {
                    String schemaName = rs.getString("TABLE_SCHEM");
                    list.add(schemaName);
                }
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        } finally {
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }

    @Override
    public List<TableMeta> listTables(String catalog, String schema, String table) {
        List<TableMeta> list = new ArrayList<>();
        Connection connection = null;
        if (table == null) {
            table = "%";
        }
        try {
            connection = connectionProvider.getConnection();
            catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getTables(catalog, schema, table, new String[]{"TABLE"})) {
                while (rs.next()) {
                    TableMeta tableMeta = new TableMeta();
                    // 获取表名
                    String tableName = rs.getString("TABLE_NAME");
                    tableMeta.setTable(tableName);
                    // 表描述
                    String remarks = rs.getString("REMARKS");
                    tableMeta.setComment(remarks);
                    tableMeta.setCatalog(catalog);
                    tableMeta.setSchema(schema);

                    // 获取表主键
                    List<String> keys = parsePrimaryKeys(metaData, catalog, schema, tableName);
                    tableMeta.setPrimaryKeys(keys);

                    // 获取表的字段
                    try (ResultSet colRs = metaData.getColumns(catalog, schema, tableName, null)) {
                        List<ColumnMeta> columnMetas = parseColumns(colRs);
                        tableMeta.setColumnMetas(columnMetas);
                    }

                    // 获取表的外键
                    try (ResultSet ikRs = metaData.getImportedKeys(catalog, schema, tableName)) {
                        Map<String, ForeignKeyMeta> fkMap = new LinkedHashMap<>();
                        while (ikRs.next()) {
                            String fkName = ikRs.getString("FK_NAME");
                            if (fkName == null) {
                                fkName = "FK_" + tableName;
                            }

                            ForeignKeyMeta fk = fkMap.computeIfAbsent(fkName, k -> {
                                ForeignKeyMeta m = new ForeignKeyMeta();
                                m.setName(k);
                                try {
                                    m.setPkTable(ikRs.getString("PKTABLE_NAME"));
                                    m.setFkTable(ikRs.getString("FKTABLE_NAME"));
                                    m.setDeleteRule(ikRs.getShort("DELETE_RULE"));
                                    m.setUpdateRule(ikRs.getShort("UPDATE_RULE"));
                                } catch (SQLException e) {
                                    throw new Sql4JException(e);
                                }
                                return m;
                            });

                            fk.addColumn(
                                    ikRs.getShort("KEY_SEQ"),
                                    ikRs.getString("FKCOLUMN_NAME"),
                                    ikRs.getString("PKCOLUMN_NAME")
                            );
                        }

                        tableMeta.setForeignKeyMetas(new ArrayList<>(fkMap.values()));
                    }

                    // 获取表的索引
                    try (ResultSet indexRs = metaData.getIndexInfo(catalog, schema, tableName, false, false)) {
                        Map<String, IndexMeta> indexMap = new LinkedHashMap<>();
                        while (indexRs.next()) {
                            String indexName = indexRs.getString("INDEX_NAME");
                            if (indexName == null) continue;

                            IndexMeta index = indexMap.computeIfAbsent(indexName, k -> {
                                IndexMeta m = new IndexMeta();
                                m.setName(k);
                                try {
                                    m.setUnique(!indexRs.getBoolean("NON_UNIQUE"));
                                } catch (SQLException e) {
                                    throw new Sql4JException(e);
                                }
                                return m;
                            });

                            String column = indexRs.getString("COLUMN_NAME");
                            if (column != null) {
                                index.addColumn(
                                        indexRs.getShort("ORDINAL_POSITION"),
                                        column,
                                        indexRs.getString("ASC_OR_DESC")
                                );
                            }
                        }
                        tableMeta.setIndexMetas(new ArrayList<>(indexMap.values()));
                    }
                    list.add(tableMeta);
                }
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        } finally {

            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }

    @Override
    public List<ViewMeta> listViews(String catalog, String schema, String viewName) {
        List<ViewMeta> list = new ArrayList<>();
        Connection connection = null;
        if (viewName == null) {
            viewName = "%";
        }
        try {
            connection = connectionProvider.getConnection();
            catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getTables(catalog, schema, viewName, new String[]{"VIEW"})) {
                while (rs.next()) {
                    ViewMeta tableMeta = new ViewMeta();
                    //获取视图名
                    String name = rs.getString("TABLE_NAME");
                    tableMeta.setViewName(name);
                    //视图描述
                    String remarks = rs.getString("REMARKS");
                    tableMeta.setComment(remarks);
                    tableMeta.setCatalog(catalog);
                    tableMeta.setSchema(schema);

                    // 获取视图的字段
                    try (ResultSet colRs = metaData.getColumns(catalog, schema, name, null)) {
                        List<ColumnMeta> columnMetas = parseColumns(colRs);
                        tableMeta.setColumnMetas(columnMetas);
                    }

                    list.add(tableMeta);
                }
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        } finally {

            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }

    @Override
    public List<FunctionMeta> listFunctions(String catalog, String schema, String function) {
        List<FunctionMeta> list = new ArrayList<>();
        Connection connection = null;
        if (function == null) {
            function = "%";
        }
        try {
            connection = connectionProvider.getConnection();
            catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet rs = metaData.getFunctions(catalog, schema, function)) {
                while (rs.next()) {
                    FunctionMeta meta = new FunctionMeta();
                    meta.setName(rs.getString("FUNCTION_NAME"));
                    meta.setCatalog(catalog);
                    meta.setSchema(schema);
                    meta.setComment(rs.getString("REMARKS"));
                    meta.setReturnType(rs.getShort("FUNCTION_TYPE"));
                    list.add(meta);
                }
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        } finally {
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }

    @Override
    public List<ProcedureMeta> listProcedures(String catalog, String schema, String procedure) {
        List<ProcedureMeta> list = new ArrayList<>();
        Connection connection = null;
        if (procedure == null) {
            procedure = "%";
        }
        try {
            connection = connectionProvider.getConnection();
            catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet rs = metaData.getProcedures(catalog, schema, procedure)) {
                while (rs.next()) {
                    ProcedureMeta meta = new ProcedureMeta();
                    meta.setName(rs.getString("PROCEDURE_NAME"));
                    meta.setCatalog(catalog);
                    meta.setSchema(schema);
                    meta.setComment(rs.getString("REMARKS"));
                    meta.setProcedureType(rs.getShort("PROCEDURE_TYPE"));
                    list.add(meta);
                }
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        } finally {
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }

    @Override
    public List<SequenceMeta> listSequences(String catalog, String schema, String sequence) {
        List<SequenceMeta> list = new ArrayList<>();
        Connection connection = null;
        if (sequence == null) {
            sequence = "%";
        }
        try {
            connection = connectionProvider.getConnection();
            catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet rs = metaData.getTables(
                    catalog,
                    schema,
                    sequence,
                    new String[]{"SEQUENCE"})) {

                while (rs.next()) {
                    SequenceMeta meta = new SequenceMeta();
                    meta.setName(rs.getString("TABLE_NAME"));
                    meta.setCatalog(catalog);
                    meta.setSchema(schema);
                    meta.setComment(rs.getString("REMARKS"));
                    list.add(meta);
                }
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        } finally {
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }
        return list;
    }


    @Override
    public List<ColumnMeta> listColumns(String catalog, String schema, String tableName) {
        List<ColumnMeta> list;
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getColumns(catalog, schema, tableName, null)) {
                list = parseColumns(rs);
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        } finally {
            if (connection != null) {
                connectionProvider.closeConnection(connection);
            }
        }

        return list;
    }

    @Override
    public List<String> listPrimaryKeys(String catalog, String schema, String tableName) {
        List<String> list;
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            list = parsePrimaryKeys(metaData, catalog, schema, tableName);

        } catch (SQLException e) {
            throw new Sql4JException(e);
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

    private List<String> parsePrimaryKeys(DatabaseMetaData metaData, String catalogsName, String schema, String tableName) {
        List<String> keys = new ArrayList<>();
        try (ResultSet rs = metaData.getPrimaryKeys(catalogsName, schema, tableName)) {
            while (rs.next()) {
                String keyName = rs.getString("COLUMN_NAME");
                keys.add(keyName);
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        }
        return keys;
    }

    /**
     * 解析table的列元数据
     * 不会关闭 rs
     */
    private List<ColumnMeta> parseColumns(ResultSet rs) throws SQLException {
        List<ColumnMeta> list = new ArrayList<>();
        while (rs.next()) {
            ColumnMeta columnMeta = new ColumnMeta();
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
        return list;
    }
}