package xyz.ytora.sql4j.orm.querygen.support;

import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.orm.querygen.AbsQueryExplain;
import xyz.ytora.sql4j.orm.querygen.token.QueryToken;
import xyz.ytora.sql4j.sql.select.SelectBuilder;
import xyz.ytora.ytool.str.Strs;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * in匹配 age_in=1,2,3 -> age in(1,2,3)
 */
public class InWhere extends AbsQueryExplain {
    @Override
    public Boolean isMatch(QueryToken token) {
        return Strs.isNotEmpty(token.getValue()) && token.getKey().endsWith("_in");
    }

    @Override
    public SelectBuilder apply(SelectBuilder selectBuilder, QueryToken token) {
        return positiveOrNegate(selectBuilder, token);
    }

    @Override
    protected SelectBuilder positive(SelectBuilder selectBuilder, QueryToken token) {
        String key = token.getKey();
        String col = Strs.toUnderline(key.substring(0, key.length() - 3));
        List<Object> values = Arrays.stream(token.getValue().split(","))
                .filter(Strs::isNotEmpty)
                .map(String::trim)
                .collect(Collectors.toList());
        if (!values.isEmpty()) {
            selectBuilder.getWhereStage().getWhere().in(Raw.of(col), values);
        }
        return selectBuilder;
    }

    @Override
    protected SelectBuilder negate(SelectBuilder selectBuilder, QueryToken token) {
        String key = token.getKey();
        String col = Strs.toUnderline(key.substring(0, key.length() - 3));
        List<Object> values = Arrays.stream(token.getValue().split(","))
                .filter(Strs::isNotEmpty)
                .map(String::trim)
                .collect(Collectors.toList());
        if (!values.isEmpty()) {
            selectBuilder.getWhereStage().getWhere().not().in(Raw.of(col), values);
        }
        return selectBuilder;
    }

    @Override
    public Integer getOrder() {
        return 0;
    }
}
