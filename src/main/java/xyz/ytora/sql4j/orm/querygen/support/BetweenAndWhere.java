package xyz.ytora.sql4j.orm.querygen.support;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.orm.querygen.AbsQueryExplain;
import xyz.ytora.sql4j.orm.querygen.token.QueryToken;
import xyz.ytora.sql4j.sql.select.SelectBuilder;
import xyz.ytora.ytool.str.Strs;

/**
 * 等值匹配，age=1，优先级最高
 */
public class BetweenAndWhere extends AbsQueryExplain {

    private static final String SUFFIX = "_between";

    @Override
    public Boolean isMatch(QueryToken token) {
        return Strs.isNotEmpty(token.getValue()) && token.getKey().endsWith(SUFFIX);
    }

    @Override
    public SelectBuilder apply(SelectBuilder where, QueryToken token) {
        return positiveOrNegate(where, token);
    }

    @Override
    protected SelectBuilder positive(SelectBuilder selectBuilder, QueryToken token) {
        String col = Strs.toUnderline(token.getKey().substring(0, token.getKey().length() - SUFFIX.length()));
        String[] values = token.getValue().split(",");
        if (values.length != 2) {
            throw new Sql4JException("参数为between模式时，必须以逗号分隔value且有两个value");
        }
        String start = values[0];
        String end = values[1];
        selectBuilder.getWhereStage().getWhere().betweenAnd(Raw.of(Strs.toUnderline(col)), start, end);
        return selectBuilder;
    }

    @Override
    protected SelectBuilder negate(SelectBuilder selectBuilder, QueryToken token) {
        String col = Strs.toUnderline(token.getKey().substring(0, token.getKey().length() - SUFFIX.length()));
        String[] values = token.getValue().split(",");
        if (values.length != 2) {
            throw new Sql4JException("参数为between模式时，必须以逗号分隔value且有两个value");
        }
        String start = values[0];
        String end = values[1];
        selectBuilder.getWhereStage().getWhere().not().betweenAnd(Raw.of(Strs.toUnderline(col)), start, end);
        return selectBuilder;
    }

    @Override
    public Integer getOrder() {
        return Integer.MIN_VALUE;
    }
}
