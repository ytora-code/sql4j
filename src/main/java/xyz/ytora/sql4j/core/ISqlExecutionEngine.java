package xyz.ytora.sql4j.core;

import xyz.ytora.sql4j.sql.SqlInfo;

/**
 * SQL执行器，将 SQL 字符串提交给数据库执行
 */
public interface ISqlExecutionEngine {

    /**
     * 执行 SQL 查询（SELECT）
     *
     * @param sqlInfo SQL 信息（包含 SQL 语句和参数）
     * @return 查询结果
     */
    ExecResult executeQuery(SqlInfo sqlInfo);

    /**
     * 执行 INSERT
     *
     * @param sqlInfo SQL 信息（包含 SQL 语句和参数）
     * @return 更新的行数
     */
    ExecResult executeInsert(SqlInfo sqlInfo);

    /**
     * 执行 UPDATE
     *
     * @param sqlInfo SQL 信息（包含 SQL 语句和参数）
     * @return 更新的行数
     */
    ExecResult executeUpdate(SqlInfo sqlInfo);

    /**
     * 执行 DELETE
     *
     * @param sqlInfo SQL 信息（包含 SQL 语句和参数）
     * @return 更新的行数
     */
    ExecResult executeDelete(SqlInfo sqlInfo);

    /**
     * 执行 DDL
     * @param sqlInfo SQL 信息
     */
    void executeDDL(SqlInfo sqlInfo);

}
