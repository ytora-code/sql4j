package xyz.yangtong.sql4j.log.support;

import xyz.yangtong.sql4j.log.ISqlLogger;

/**
 * 默认的 SQL 日志记录器
 */
public class DefaultSqlLogger implements ISqlLogger {
    @Override
    public void error(Object msg) {
        System.err.println(msg);
    }

    @Override
    public void warn(Object msg) {
        System.out.println(msg);
    }

    @Override
    public void info(Object msg) {
        System.out.println(msg);
    }

    @Override
    public void debug(Object msg) {
        System.out.println(msg);
    }
}
