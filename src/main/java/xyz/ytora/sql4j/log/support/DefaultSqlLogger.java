package xyz.ytora.sql4j.log.support;

import xyz.ytora.sql4j.log.ISqlLogger;
import xyz.ytora.ytool.str.Strs;

/**
 * 默认的 SQL 日志记录器
 */
public class DefaultSqlLogger implements ISqlLogger {
    @Override
    public void error(Object msg, Object... args) {
        System.err.println(Strs.format(String.valueOf(msg), args));
    }

    @Override
    public void warn(Object msg, Object... args) {
        System.out.println(Strs.format(String.valueOf(msg), args));
    }

    @Override
    public void info(Object msg, Object... args) {
        System.out.println(Strs.format(String.valueOf(msg), args));
    }

    @Override
    public void debug(Object msg, Object... args) {
        System.out.println(Strs.format(String.valueOf(msg), args));
    }
}
