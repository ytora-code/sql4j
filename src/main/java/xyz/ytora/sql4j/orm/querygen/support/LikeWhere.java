package xyz.ytora.sql4j.orm.querygen.support;

import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.orm.querygen.AbsQueryExplain;
import xyz.ytora.sql4j.orm.querygen.token.QueryToken;
import xyz.ytora.sql4j.sql.select.SelectBuilder;
import xyz.ytora.ytool.str.Strs;

/**
 模糊查询，支持全模糊，左模糊，右模糊，name=*张三*，优先级仅次于等值查询
 */
public class LikeWhere extends AbsQueryExplain {

    private static final String SUFFIX = "_le";

    @Override
    public Boolean isMatch(QueryToken token) {
        String v = token.getValue();
        return Strs.isNotEmpty(v) && v.startsWith("*") && v.endsWith("*");
    }

    @Override
    public SelectBuilder apply(SelectBuilder selectBuilder, QueryToken token) {
        return positiveOrNegate(selectBuilder, token);
    }

    @Override
    protected SelectBuilder positive(SelectBuilder selectBuilder, QueryToken token) {
        String col = Strs.toUnderline(token.getKey());
        String pat = token.getValue().substring(1, token.getValue().length() - 1);
        selectBuilder.getWhereStage().getWhere().like(Raw.of(Strs.toUnderline(col)), "%" + pat + "%");
        return selectBuilder;
    }

    @Override
    protected SelectBuilder negate(SelectBuilder selectBuilder, QueryToken token) {
        String col = Strs.toUnderline(token.getKey());
        String pat = token.getValue().substring(1, token.getValue().length() - 1);
        selectBuilder.getWhereStage().getWhere().not().like(Raw.of(Strs.toUnderline(col)), "%" + pat + "%");
        return selectBuilder;
    }

    @Override
    public Integer getOrder() {
        return Integer.MIN_VALUE + 1;
    }
}
