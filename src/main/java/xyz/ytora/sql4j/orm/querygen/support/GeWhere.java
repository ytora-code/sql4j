package xyz.ytora.sql4j.orm.querygen.support;

import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.orm.querygen.AbsQueryExplain;
import xyz.ytora.sql4j.orm.querygen.token.QueryToken;
import xyz.ytora.sql4j.sql.select.SelectBuilder;
import xyz.ytora.ytool.str.Strs;

/**
 * 大于等于  age_ge=23 -> age >= 23
 */
public class GeWhere extends AbsQueryExplain {

    private static final String SUFFIX = "_ge";

    @Override
    public Boolean isMatch(QueryToken token) {
        return Strs.isNotEmpty(token.getValue()) && token.getKey().endsWith(SUFFIX);
    }

    @Override
    public SelectBuilder apply(SelectBuilder selectBuilder, QueryToken token) {
        return positiveOrNegate(selectBuilder, token);
    }

    @Override
    protected SelectBuilder positive(SelectBuilder selectBuilder, QueryToken token) {
        String col = Strs.toUnderline(token.getKey().substring(0, token.getKey().length() - SUFFIX.length()));
        selectBuilder.getWhereStage().getWhere().ge(Raw.of(col), token.getValue());
        return selectBuilder;
    }

    @Override
    protected SelectBuilder negate(SelectBuilder selectBuilder, QueryToken token) {
        String col = Strs.toUnderline(token.getKey().substring(0, token.getKey().length() - SUFFIX.length()));
        // >= 的反义词是 <
        selectBuilder.getWhereStage().getWhere().lt(Raw.of(col), token.getValue());
        return selectBuilder;
    }

    @Override
    public Integer getOrder() {
        return 0;
    }
}
