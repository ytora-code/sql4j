package xyz.ytora.sql4j.sql;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.util.Sql4jUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 包装参数
 * <pre />
 * SQL 里面的普通参数最终会被解析成 ‘?’，如果想要原样拼接，则要给参数包一层 Wrapper
 */
public record Wrapper(Object value) implements SFunction<Object, Object> {

    public String getRealValue() {
        return Sql4jUtil.formatVal(value);
    }

    public static Wrapper of(Object value) {
        return new Wrapper(value);
    }

    @Override
    public Object apply(Object o) {
        throw new UnsupportedOperationException();
    }
}
