package xyz.yangtong.sql4j.sql;

import xyz.yangtong.sql4j.func.SFunction;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 包装参数
 * <pre />
 * SQL 里面的普通参数最终会被解析成 ‘?’，如果想要原样拼接，则要给参数包一层 Wrapper
 */
public class Wrapper implements SFunction<Object, Object> {
    private final Object value;

    public Wrapper(Object value) {
        this.value = value;
    }

    public String getRealValue() {
        return formatVal(value);
    }

    public static Wrapper of(Object value) {
        return new Wrapper(value);
    }

    /**
     * 把 Java 值格式化为 SQL 字面量
     */
    private String formatVal(Object val) {
        if (val == null) {
            return "NULL";
        }
        if (val instanceof String || val instanceof Character) {
            return "'" + escapeSingleQuote(String.valueOf(val)) + "'";
        }
        if (val instanceof BigDecimal) {
            return ((BigDecimal) val).toPlainString();
        }
        if (val instanceof Number) {
            return val.toString();
        }
        if (val instanceof Boolean) {
            // 按常见习惯转 1/0，也可以根据数据库定制
            return ((Boolean) val) ? "1" : "0";
        }
        if (val instanceof Date) {
            // 简单处理，实际项目建议使用占位符+参数绑定
            return "'" + val + "'";
        }
        // 其他类型统一按字符串处理
        return "'" + escapeSingleQuote(val.toString()) + "'";
    }

    /**
     * 单引号转义
     */
    private String escapeSingleQuote(String str) {
        return str.replace("'", "''");
    }

    @Override
    public Object apply(Object o) {
        throw new UnsupportedOperationException();
    }
}
