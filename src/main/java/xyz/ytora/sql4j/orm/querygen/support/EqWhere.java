package xyz.ytora.sql4j.orm.querygen.support;

import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.orm.querygen.AbsQueryExplain;
import xyz.ytora.sql4j.orm.querygen.token.QueryToken;
import xyz.ytora.sql4j.sql.select.SelectBuilder;
import xyz.ytora.ytool.str.Strs;

/**
 * 等值匹配，age=1，优先级最高
 */
public class EqWhere extends AbsQueryExplain {
    @Override
    public Boolean isMatch(QueryToken token) {
        String k = token.getKey();
        String v = token.getValue();
        if (Strs.isEmpty(k) || Strs.isEmpty(v)) return false;
        if (k.endsWith("_gt") || k.endsWith("_ge") || k.endsWith("_lt") || k.endsWith("_le")
                || k.endsWith("_in") || k.endsWith("_or") || k.equals("orderCol") || k.equals("selectCol")) return false;
        return !v.startsWith("*") || !v.endsWith("*");
    }

    @Override
    public SelectBuilder apply(SelectBuilder selectBuilder, QueryToken token) {
        return positiveOrNegate(selectBuilder, token);
    }

    @Override
    protected SelectBuilder positive(SelectBuilder selectBuilder, QueryToken token) {
        selectBuilder.getWhereStage().getWhere().eq(Raw.of(Strs.toUnderline(token.getKey())), token.getValue());
        return selectBuilder;
    }

    @Override
    protected SelectBuilder negate(SelectBuilder selectBuilder, QueryToken token) {
        selectBuilder.getWhereStage().getWhere().ne(Raw.of(Strs.toUnderline(token.getKey())), token.getValue());
        return selectBuilder;
    }

    @Override
    public Integer getOrder() {
        return Integer.MIN_VALUE;
    }
}
