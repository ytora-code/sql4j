package org.ytor.sql4j.core.support;

import org.ytor.sql4j.core.ExecResult;
import org.ytor.sql4j.core.IConnectionProvider;
import org.ytor.sql4j.core.ISqlExecutionEngine;
import org.ytor.sql4j.enums.DatabaseType;
import org.ytor.sql4j.sql.SqlInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL 执行引擎
 */
public class SqlExecutionEngine implements ISqlExecutionEngine {

    /**
     * 数据库连接提供者
     */
    private final IConnectionProvider connectionProvider;

    public SqlExecutionEngine(IConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public ExecResult executeQuery(SqlInfo sqlInfo) throws SQLException {
        long startTime = System.currentTimeMillis();
        try (Connection connection = connectionProvider.getConnection()) {
            // 获取数据库的元数据
            DatabaseMetaData connectionMetaData = connection.getMetaData();
            String sql = sqlInfo.getSql();
            List<Object> params = sqlInfo.getOrderedParms();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                setParameters(statement, params);

                try (ResultSet resultSet = statement.executeQuery()) {
                    List<Map<String, Object>> resultList = new ArrayList<>();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // 处理查询结果
                    while (resultSet.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                        }
                        resultList.add(row);
                    }
                    // 创建并返回执行结果
                    return createExecResult(sqlInfo, DatabaseType.fromString(connectionMetaData.getDatabaseProductName()), resultList, null, null, System.currentTimeMillis() - startTime);
                }
            }
        }
    }

    @Override
    public ExecResult executeInsert(SqlInfo sqlInfo) throws SQLException {
        long startTime = System.currentTimeMillis();
        try (Connection connection = connectionProvider.getConnection()) {
            // 获取数据库的元数据
            DatabaseMetaData connectionMetaData = connection.getMetaData();
            String sql = sqlInfo.getSql();
            List<Object> params = sqlInfo.getOrderedParms();

            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setParameters(statement, params);
                int affectedRows = statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    List<Object> ids = new ArrayList<>();
                    while (generatedKeys.next()) {
                        ids.add(generatedKeys.getObject(1));
                    }
                    return createExecResult(sqlInfo, DatabaseType.fromString(connectionMetaData.getDatabaseProductName()), null, (long) affectedRows, ids, System.currentTimeMillis() - startTime);
                }
            }
        }
    }

    @Override
    public ExecResult executeUpdate(SqlInfo sqlInfo) throws SQLException {
        long startTime = System.currentTimeMillis();
        try (Connection connection = connectionProvider.getConnection()) {
            // 获取数据库的元数据
            DatabaseMetaData connectionMetaData = connection.getMetaData();
            String sql = sqlInfo.getSql();
            List<Object> params = sqlInfo.getOrderedParms();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                setParameters(statement, params);
                int affectedRows = statement.executeUpdate();
                return createExecResult(sqlInfo, DatabaseType.fromString(connectionMetaData.getDatabaseProductName()), null, (long) affectedRows, null, System.currentTimeMillis() - startTime);
            }
        }
    }

    @Override
    public ExecResult executeDelete(SqlInfo sqlInfo) throws SQLException {
        // DELETE 和 UPDATE 的处理方式一样
        return executeUpdate(sqlInfo);
    }

    /**
     * SQL 参数绑定
     */
    private void setParameters(PreparedStatement statement, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            statement.setObject(i + 1, params.get(i));
        }
    }

    /**
     * 创建 ExecResult 对象
     */
    private ExecResult createExecResult(SqlInfo sqlInfo, DatabaseType databaseType, List<Map<String, Object>> resultList,
                                        Long effectedRows, List<Object> ids, Long executionTime) {
        ExecResult execResult = new ExecResult();
        execResult.setSqlInfo(sqlInfo);
        execResult.setDatabaseType(databaseType);
        execResult.setResultList(resultList);
        execResult.setEffectedRows(effectedRows);
        execResult.setIds(ids);
        execResult.setExecutionTime(executionTime);
        return execResult;
    }
}