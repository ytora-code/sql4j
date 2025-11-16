package org.ytor.sql4j.log;

/**
 * SQL日志记录
 */
public interface ISqlLogger {

    /**
     * 错误日志
     */
    void error(Object msg);

    /**
     * 警告日志
     */
    void warn(Object msg);

    /**
     * 正常信息日志
     */
    void info(Object msg);

    /**
     * debug日志
     */
    void debug(Object msg);
}
