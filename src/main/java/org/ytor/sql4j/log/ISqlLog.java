package org.ytor.sql4j.log;

/**
 * SQL日志记录
 */
public interface ISqlLog {

    /**
     * 错误日志
     */
    void error(String msg);

    /**
     * 警告日志
     */
    void warn(String msg);

    /**
     * 正常信息日志
     */
    void info(String msg);

    /**
     * debug日志
     */
    void debug(String msg);
}
