package org.ytor.sql4j.core.support;

import org.ytor.sql4j.Sql4JException;
import org.ytor.sql4j.caster.SQLWriter;
import org.ytor.sql4j.core.ExecResult;
import org.ytor.sql4j.core.IConnectionProvider;
import org.ytor.sql4j.core.ISqlExecutionEngine;
import org.ytor.sql4j.core.SQLHelper;
import org.ytor.sql4j.enums.DatabaseType;
import org.ytor.sql4j.interceptor.SqlInterceptor;
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
     * SQLHelper
     */
    private final SQLHelper sqlHelper;

    /**
     * 数据库连接提供者
     */
    private final IConnectionProvider connectionProvider;

    public SqlExecutionEngine(SQLHelper sqlHelper) {
        this.sqlHelper = sqlHelper;
        this.connectionProvider = sqlHelper.getConnectionProvider();
    }

    @Override
    public ExecResult executeQuery(SqlInfo sqlInfo) {
        long startTime = System.currentTimeMillis();
        if (!before(sqlHelper.getSqlInterceptors(), sqlInfo)) {
            return createExecResult(sqlInfo, null, new ArrayList<>(), null, null, System.currentTimeMillis() - startTime, 1);
        }

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

                        // 记录 SQL 执行结果
                        sqlHelper.getLogger().debug(" <===\t" + row);
                    }
                    // 创建并返回执行结果
                    ExecResult execResult = createExecResult(sqlInfo, DatabaseType.fromString(connectionMetaData.getDatabaseProductName()), resultList, null, null, System.currentTimeMillis() - startTime, 0);
                    return after(sqlHelper.getSqlInterceptors(), sqlInfo, execResult);
                }
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        }
    }

    @Override
    public ExecResult executeInsert(SqlInfo sqlInfo) {
        long startTime = System.currentTimeMillis();
        if (!before(sqlHelper.getSqlInterceptors(), sqlInfo)) {
            return createExecResult(sqlInfo, null, new ArrayList<>(), null, null, System.currentTimeMillis() - startTime, 1);
        }

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

                    // 记录 SQL 执行结果
                    sqlHelper.getLogger().debug(" <===\t 新增行数：" + affectedRows);
                    ExecResult execResult = createExecResult(sqlInfo, DatabaseType.fromString(connectionMetaData.getDatabaseProductName()), null, affectedRows, ids, System.currentTimeMillis() - startTime, 0);
                    return after(sqlHelper.getSqlInterceptors(), sqlInfo, execResult);
                }
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        }
    }

    @Override
    public ExecResult executeUpdate(SqlInfo sqlInfo) {
        long startTime = System.currentTimeMillis();
        if (!before(sqlHelper.getSqlInterceptors(), sqlInfo)) {
            return createExecResult(sqlInfo, null, new ArrayList<>(), null, null, System.currentTimeMillis() - startTime, 1);
        }

        try (Connection connection = connectionProvider.getConnection()) {
            // 获取数据库的元数据
            DatabaseMetaData connectionMetaData = connection.getMetaData();
            String sql = sqlInfo.getSql();
            List<Object> params = sqlInfo.getOrderedParms();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                setParameters(statement, params);
                int affectedRows = statement.executeUpdate();

                // 记录 SQL 执行结果
                sqlHelper.getLogger().debug(" <===\t 影响行数" + affectedRows);
                ExecResult execResult = createExecResult(sqlInfo, DatabaseType.fromString(connectionMetaData.getDatabaseProductName()), null, affectedRows, null, System.currentTimeMillis() - startTime, 0);
                return after(sqlHelper.getSqlInterceptors(), sqlInfo, execResult);
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        }
    }

    @Override
    public ExecResult executeDelete(SqlInfo sqlInfo) {
        // DELETE 和 UPDATE 的处理方式一样
        return executeUpdate(sqlInfo);
    }

    /**
     * SQL 参数绑定
     */
    private void setParameters(PreparedStatement statement, List<Object> params) {
        try {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                // 如果 param 重写了 SQLWriter, 则回调 SQLWriter 的 write 方法作为真实的参数
                if (param instanceof SQLWriter) {
                    SQLWriter writer = (SQLWriter) param;
                    param = writer.write();
                }
                statement.setObject(i + 1, param);
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        }

    }

    /**
     * 创建 ExecResult 对象
     */
    private ExecResult createExecResult(SqlInfo sqlInfo, DatabaseType databaseType, List<Map<String, Object>> resultList,
                                        Integer effectedRows, List<Object> ids, Long executionTime, Integer status) {
        ExecResult execResult = new ExecResult();
        execResult.setSqlHelper(sqlHelper);
        execResult.setSqlInfo(sqlInfo);
        execResult.setDatabaseType(databaseType);
        execResult.setResultList(resultList);
        execResult.setEffectedRows(effectedRows);
        execResult.setIds(ids);
        execResult.setExecutionTime(executionTime);
        if (status != null) {
            status = 0;
        }
        execResult.setStatus(status);
        return execResult;
    }

    private Boolean before(List<SqlInterceptor> beforeInterceptors, SqlInfo sqlInfo) {
        for (SqlInterceptor interceptor : beforeInterceptors) {
            Boolean before = interceptor.before(sqlInfo);
            if (!before) {
                return false;
            }
        }
        return true;
    }

    private ExecResult after(List<SqlInterceptor> beforeInterceptors, SqlInfo sqlInfo, ExecResult result) {
        for (SqlInterceptor interceptor : beforeInterceptors) {
            result = interceptor.after(sqlInfo, result);
        }
        return result;
    }
}