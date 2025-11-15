package org.ytor.sql4j.core;

import org.ytor.sql4j.sql.SqlInfo;

import java.sql.SQLException;

/**
 * SQL执行器，将 SQL 字符串提交给数据库执行
 */
public interface ISqlExecutionEngine {

    /**
     * 执行 SQL 查询（SELECT）
     *
     * @param sqlInfo SQL 信息（包含 SQL 语句和参数）
     * @return 查询结果
     * @throws SQLException SQL 执行时抛出的异常
     */
    ExecResult executeQuery(SqlInfo sqlInfo) throws SQLException;

    /**
     * 执行 INSERT
     *
     * @param sqlInfo SQL 信息（包含 SQL 语句和参数）
     * @return 更新的行数
     * @throws SQLException SQL 执行时抛出的异常
     */
    ExecResult executeInsert(SqlInfo sqlInfo) throws SQLException;

    /**
     * 执行 UPDATE
     *
     * @param sqlInfo SQL 信息（包含 SQL 语句和参数）
     * @return 更新的行数
     * @throws SQLException SQL 执行时抛出的异常
     */
    ExecResult executeUpdate(SqlInfo sqlInfo) throws SQLException;

    /**
     * 执行 DELETE
     *
     * @param sqlInfo SQL 信息（包含 SQL 语句和参数）
     * @return 更新的行数
     * @throws SQLException SQL 执行时抛出的异常
     */
    ExecResult executeDelete(SqlInfo sqlInfo) throws SQLException;

}
