package xyz.ytora.sql4j.log;

/**
 * SQL日志记录
 */
public interface ISqlLogger {

    /**
     * 错误日志
     */
    void error(Object msg, Object... args);

    /**
     * 警告日志
     */
    void warn(Object msg, Object... args);

    /**
     * 正常信息日志
     */
    void info(Object msg, Object... args);

    /**
     * debug日志
     */
    void debug(Object msg, Object... args);
}
