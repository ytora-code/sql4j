package xyz.ytora.sql4j.orm.querygen.support;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.orm.querygen.AbsQueryExplain;
import xyz.ytora.sql4j.orm.querygen.token.QueryToken;
import xyz.ytora.sql4j.sql.select.SelectBuilder;
import xyz.ytora.sql4j.sql.select.SelectStage;
import xyz.ytora.ytool.str.Strs;

import java.util.ArrayList;
import java.util.List;

/**
 * 排序字段，固定字段名称：orderCol，↑表示升序，↓表示降序，orderCol=id↑,userName↓ 表示先按id升序，再按userName降序
 */
public class SelectCol extends AbsQueryExplain {

    @Override
    public Boolean isMatch(QueryToken token) {
        return token.getKey().equals("selectCol");
    }

    @Override
    public SelectBuilder apply(SelectBuilder selectBuilder, QueryToken token) {
        return positiveOrNegate(selectBuilder, token);
    }

    @Override
    protected SelectBuilder positive(SelectBuilder selectBuilder, QueryToken token) {
        String[] orderCols = token.getValue().split(",");
        SelectStage selectStage = selectBuilder.getSelectStage();
        for (String orderCol : orderCols) {
            if (selectStage == null) {
                List<SFunction<?, ?>> list = new ArrayList<>();
                list.add(Raw.of(Strs.toUnderline(orderCol)));
                selectStage = new SelectStage(selectBuilder, list);
            } else {
                selectStage.select(Raw.of(Strs.toUnderline(orderCol)));
            }
        }
        selectBuilder.setSelectStage(selectStage);
        return selectBuilder;
    }

    @Override
    protected SelectBuilder negate(SelectBuilder selectBuilder, QueryToken token) {
        return selectBuilder;
    }

    @Override
    public Integer getOrder() {
        return 1;
    }
}
