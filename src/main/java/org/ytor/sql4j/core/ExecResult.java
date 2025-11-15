package org.ytor.sql4j.core;

import org.ytor.sql4j.enums.DatabaseType;
import org.ytor.sql4j.sql.SqlInfo;

import java.util.List;
import java.util.Map;

/**
 * SQL执行结果
 */
public class ExecResult {

    /**
     * 执行的 SQL 信息
     */
    private SqlInfo sqlInfo;

    /**
     * 数据库类型
     */
    private DatabaseType databaseType;

    /**
     * SELECT 执行结果（仅当 SQL 类型是 SELECT 时该字段才有值）
     */
    private List<Map<String, Object>> resultList;

    /**
     * 受影响的行数（仅当 SQL 类型是 INSERT、DELETE、UPDATE 时该字段才有值）
     */
    private Long effectedRows;

    /**
     * 数据新增后，产生的主键ID（仅当 SQL 类型是 INSERT 时该字段才有值）
     */
    private List<Object> ids;

    /**
     * SQL 执行时间
     */
    private Long executionTime;

    /**
     * SQL 执行错误信息
     */
    private String errorMessage;

    /**
     *  执行是否成功
     */
    private Boolean isSuccess;

    public SqlInfo getSqlInfo() {
        return sqlInfo;
    }

    public void setSqlInfo(SqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public List<Map<String, Object>> getResultList() {
        return resultList;
    }

    public void setResultList(List<Map<String, Object>> resultList) {
        this.resultList = resultList;
    }

    public Long getEffectedRows() {
        return effectedRows;
    }

    public void setEffectedRows(Long effectedRows) {
        this.effectedRows = effectedRows;
    }

    public List<Object> getIds() {
        return ids;
    }

    public void setIds(List<Object> ids) {
        this.ids = ids;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }
}
