package xyz.ytora.sql4j.core.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.caster.SQLWriter;
import xyz.ytora.sql4j.core.ExecResult;
import xyz.ytora.sql4j.core.IConnectionProvider;
import xyz.ytora.sql4j.core.ISqlExecutionEngine;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.enums.DbType;
import xyz.ytora.sql4j.enums.SqlType;
import xyz.ytora.sql4j.interceptor.SqlInterceptor;
import xyz.ytora.sql4j.sql.SqlInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL 执行引擎
 */
public class SqlExecutionEngine implements ISqlExecutionEngine {

    private static final Logger log = LoggerFactory.getLogger(SqlExecutionEngine.class);
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
    public ExecResult executeSelect(SqlInfo sqlInfo) {
        check(sqlInfo);
        long startTime = System.currentTimeMillis();
        if (!before(sqlHelper.getSqlInterceptors(), sqlInfo)) {
            return createExecResult(sqlInfo, null, new ArrayList<>(), null, null, System.currentTimeMillis() - startTime, 1);
        }

        Connection connection = connectionProvider.getConnection();
        try {
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
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                        }
                        resultList.add(row);

                        // 记录 SQL 执行结果
                        sqlHelper.getLogger().debug(" <===\t" + row);
                    }
                    // 创建并返回执行结果
                    ExecResult execResult = createExecResult(sqlInfo, DbType.fromString(connectionMetaData.getDatabaseProductName()), resultList, null, null, System.currentTimeMillis() - startTime, 0);
                    return after(sqlHelper.getSqlInterceptors(), sqlInfo, execResult);
                }
            }
        } catch (SQLException e) {
            sqlHelper.getLogger().error(e.getMessage());
            throw new Sql4JException(e);
        } finally {
            connectionProvider.closeConnection(connection);
        }
    }

    @Override
    public ExecResult executeInsert(SqlInfo sqlInfo) {
        check(sqlInfo);
        long startTime = System.currentTimeMillis();
        if (!before(sqlHelper.getSqlInterceptors(), sqlInfo)) {
            return createExecResult(sqlInfo, null, new ArrayList<>(), null, null, System.currentTimeMillis() - startTime, 1);
        }

        Connection connection = connectionProvider.getConnection();
        try {
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
                    ExecResult execResult = createExecResult(sqlInfo, DbType.fromString(connectionMetaData.getDatabaseProductName()), null, affectedRows, ids, System.currentTimeMillis() - startTime, 0);
                    return after(sqlHelper.getSqlInterceptors(), sqlInfo, execResult);
                }
            }
        } catch (SQLException e) {
            sqlHelper.getLogger().error(e.getMessage());
            throw new Sql4JException(e);
        } finally {
            connectionProvider.closeConnection(connection);
        }
    }

    @Override
    public ExecResult executeUpdate(SqlInfo sqlInfo) {
        check(sqlInfo);
        long startTime = System.currentTimeMillis();
        if (!before(sqlHelper.getSqlInterceptors(), sqlInfo)) {
            return createExecResult(sqlInfo, null, new ArrayList<>(), null, null, System.currentTimeMillis() - startTime, 1);
        }

        Connection connection = connectionProvider.getConnection();
        try {
            // 获取数据库的元数据
            DatabaseMetaData connectionMetaData = connection.getMetaData();
            String sql = sqlInfo.getSql();
            List<Object> params = sqlInfo.getOrderedParms();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                setParameters(statement, params);
                int affectedRows = statement.executeUpdate();

                // 记录 SQL 执行结果
                sqlHelper.getLogger().debug(" <===\t 影响行数" + affectedRows);
                ExecResult execResult = createExecResult(sqlInfo, DbType.fromString(connectionMetaData.getDatabaseProductName()), null, affectedRows, null, System.currentTimeMillis() - startTime, 0);
                return after(sqlHelper.getSqlInterceptors(), sqlInfo, execResult);
            }
        } catch (SQLException e) {
            sqlHelper.getLogger().error(e.getMessage());
            throw new Sql4JException(e);
        } finally {
            connectionProvider.closeConnection(connection);
        }
    }

    @Override
    public ExecResult executeDelete(SqlInfo sqlInfo) {
        // DELETE 和 UPDATE 的处理方式一样
        return executeUpdate(sqlInfo);
    }

    @Override
    public void executeDDL(SqlInfo sqlInfo) {
        if (sqlInfo.getSqlType() != SqlType.DDL) {
            throw new Sql4JException(sqlInfo.getSqlType() + " 不支持调用 executeDDL");
        }
        check(sqlInfo);
        sqlHelper.getLogger().info(" ==== 即将执行DDL: " + sqlInfo.getSql());

        Connection connection = connectionProvider.getConnection();
        try {
            // 获取数据库的元数据
            String sql = sqlInfo.getSql();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // 执行 DDL 操作
                statement.executeUpdate();

                sqlHelper.getLogger().info(" ==== DDL执行成功");
            }
        } catch (SQLException e) {
            sqlHelper.getLogger().info(" ==== DDL执行失败: " + e.getMessage());
            throw new Sql4JException(e);
        } finally {
            connectionProvider.closeConnection(connection);
        }
    }

    /**
     * SQL 参数绑定
     */
    private void setParameters(PreparedStatement statement, List<Object> params) {
        try {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param != null) {
                    // 如果 param 重写了 SQLWriter, 则回调 SQLWriter 的 write 方法作为真实的参数
                    if (param instanceof SQLWriter writer) {
                        param = writer.write();
                    }
                    // 如果是枚举，则转为字符串
                    else if (param.getClass().isEnum()) {
                        param = ((Enum<?>) param).name();
                    }
                }
                statement.setObject(i + 1, param);
            }
        } catch (SQLException e) {
            sqlHelper.getLogger().error(e.getMessage());
            throw new Sql4JException(e);
        }

    }

    /**
     * 创建 ExecResult 对象
     */
    private ExecResult createExecResult(SqlInfo sqlInfo, DbType databaseType, List<Map<String, Object>> resultList,
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
        if (!sqlInfo.getInterceptorEnabled()) {
            return true;
        }
        for (SqlInterceptor interceptor : beforeInterceptors) {
            Boolean before = interceptor.before(sqlInfo);
            if (!before) {
                return false;
            }
        }
        return true;
    }

    private ExecResult after(List<SqlInterceptor> beforeInterceptors, SqlInfo sqlInfo, ExecResult result) {
        if (!sqlInfo.getInterceptorEnabled()) {
            return result;
        }
        for (SqlInterceptor interceptor : beforeInterceptors) {
            result = interceptor.after(sqlInfo, result);
        }
        return result;
    }

    /**
     * 检查即将执行的 SQL 有没有 DROP、TRUNCATE 这种危险语句
     * @param sqlInfo
     */
    private void check(SqlInfo sqlInfo) {
        String sqls = sqlInfo.getSql();
        String[] sqlArr = sqls.split(";");
        for (String sql : sqlArr) {
            String[] segments = sql.split(" ");
            for (String segment : segments) {
                if ("DROP".equalsIgnoreCase(segment)) {
                    throw new Sql4JException("危险!!!即将执行的SQL中包含关键字[DROP]，已被拦截。如果你是故意这样做，请更换ORM框架：[" + sql + "]");
                }
                if ("TRUNCATE".equalsIgnoreCase(segment)) {
                    throw new Sql4JException("危险!!!即将执行的SQL中包含关键字[TRUNCATE]，已被拦截。如果你是故意这样做，请更换ORM框架：[" + sql + "]");
                }
            }
        }
    }
}