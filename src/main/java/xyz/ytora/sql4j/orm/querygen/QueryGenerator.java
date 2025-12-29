package xyz.ytora.sql4j.orm.querygen;

import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.orm.querygen.support.*;
import xyz.ytora.sql4j.orm.querygen.token.QueryToken;
import xyz.ytora.sql4j.orm.querygen.token.QueryTokenizer;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.select.SelectBuilder;
import xyz.ytora.sql4j.sql.select.SelectStage;
import xyz.ytora.sql4j.sql.select.SelectWhereStage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * query 产生器，根据字符串产生 SelectBuilder
 */
public class QueryGenerator {

    /**
     * 忽略请求中的这些字段
     */
    private static final List<String> ignoreFields = List.of("pageNo", "pageSize");

    /**
     * 条件判断
     */
    private static final List<AbsQueryExplain> conditions = new ArrayList<>();

    // 注册查询解释器
    static {
        conditions.add(new EqWhere());
        conditions.add(new BetweenAndWhere());
        conditions.add(new GeWhere());
        conditions.add(new GtWhere());
        conditions.add(new LeWhere());
        conditions.add(new LtWhere());
        conditions.add(new LikeWhere());
        conditions.add(new InWhere());
        conditions.add(new OrderCol());
        conditions.add(new SelectCol());
    }

    public static SelectBuilder where(String queryString) {
        SelectBuilder selectBuilder = new SelectBuilder(SQLHelper.getInstance());

        selectBuilder.setSelectStage(new SelectStage(selectBuilder, new ArrayList<>()));
        selectBuilder.setWhereStage(new SelectWhereStage(selectBuilder, new ConditionExpressionBuilder(selectBuilder)));

        if (queryString != null) {
            List<QueryToken> tokens = Arrays.stream(queryString.split("&")).filter(i -> {
                String key = i.split("=")[0];
                return !ignoreFields.contains(key);
            }).map(QueryTokenizer::tokenize).toList();

            for (QueryToken token : tokens) {
                for (AbsQueryExplain queryCondition : conditions) {
                    if (queryCondition.isMatch(token)) {
                        queryCondition.apply(selectBuilder, token);
                        break;
                    }
                }
            }
        }
        return selectBuilder;
    }
}
