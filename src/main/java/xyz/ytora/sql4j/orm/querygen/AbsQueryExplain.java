package xyz.ytora.sql4j.orm.querygen;

import xyz.ytora.sql4j.orm.querygen.token.QueryToken;
import xyz.ytora.sql4j.sql.select.SelectBuilder;

/**
 * 抽象的查询解释器
 */
public abstract class AbsQueryExplain {
    /**
     * 判断当前解释器是否支持该 TOKEN
     */
    public abstract Boolean isMatch(QueryToken token);

    /**
     * 将 TOKEN 拼接到 SelectBuilder
     */
    public abstract SelectBuilder apply(SelectBuilder selectBuilder, QueryToken token);

    /**
     * 正、逆逻辑分发
     */
    protected SelectBuilder positiveOrNegate(SelectBuilder selectBuilder, QueryToken token) {
        return token.isPositive() ? positive(selectBuilder, token) : negate(selectBuilder, token);
    }

    /**
     * 正向匹配（如 =、>、IN、LIKE 等）
     */
    protected abstract SelectBuilder positive(SelectBuilder selectBuilder, QueryToken token);

    /**
     * 逆向匹配（如 !=、NOT IN、NOT LIKE…）
     */
    protected abstract SelectBuilder negate(SelectBuilder selectBuilder, QueryToken token);

    public Integer getOrder() {
        return 0;
    }
}
