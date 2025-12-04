package xyz.ytora.sql4j.func;

import xyz.ytora.sql4j.func.support.*;

/**
 * 函数聚合类，聚合了SQL 内部的场景函数，也可以自定义
 */
public class SQLFuncAggregation {

    public static Raw raw(String rawStr) {
        return Raw.of(rawStr);
    }

    public static <T> Count count(SFunction<T, ?> column) {
        return Count.of(column);
    }

    public static <T> Count count(String str) {
        return Count.of(str);
    }

    public static <T> Length length(String str) {
        return Length.of(str);
    }

    public static <T> Max max(SFunction<T, ?> column) {
        return Max.of(column);
    }

    public static <T> Min min(SFunction<T, ?> column) {
        return Min.of(column);
    }

    public static <T> Sum sum(SFunction<T, ?> column) {
        return Sum.of(column);
    }

    public static <T> Avg avg(SFunction<T, ?> column) {
        return Avg.of(column);
    }

    public static Now now() {
        return Now.of();
    }

}
