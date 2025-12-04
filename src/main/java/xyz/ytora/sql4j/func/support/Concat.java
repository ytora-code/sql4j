package xyz.ytora.sql4j.func.support;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.AliasRegister;
import xyz.ytora.sql4j.sql.Wrapper;
import xyz.ytora.sql4j.util.LambdaUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于连接多个字符串。
 */
public class Concat implements SQLFunc {

    private List<SFunction<?, ?>> columns = new ArrayList<>();

    private String as;

    private AliasRegister aliasRegister;

    public <T> Concat(List<SFunction<T, ?>> columns) {
        this.columns.addAll(columns);
    }

    @SafeVarargs
    public static <T> Concat of(SFunction<T, ?>... columns) {
        return new Concat(Arrays.asList(columns));
    }

    @Override
    public SQLFunc as(String as) {
        this.as = as;
        return this;
    }

    @Override
    public String as() {
        return as;
    }

    @Override
    public void addAliasRegister(AliasRegister aliasRegister) {
        this.aliasRegister = aliasRegister;
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder("concat(");
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            SFunction<?, ?> col = columns.get(i);
            if (col instanceof Wrapper) {
                sb.append(((Wrapper) col).getRealValue());
            } else {
                sb.append(LambdaUtil.parseColumn(col, aliasRegister));
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
