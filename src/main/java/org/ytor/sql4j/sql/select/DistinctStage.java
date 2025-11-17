package org.ytor.sql4j.sql.select;

import org.ytor.sql4j.func.SFunction;

import java.util.Arrays;
import java.util.Collections;

/**
 * DISTINCT 阶段
 */
public class DistinctStage extends AbsSelect {

    public DistinctStage(SelectBuilder selectBuilder) {
        setSelectBuilder(selectBuilder);
        getSelectBuilder().setDistinctStage(this);
    }

    /**
     * DISTINCT 后必须是 SELECT
     */
    @SafeVarargs
    public final <T> SelectStage select(SFunction<T, ?>... columns) {
        return new SelectStage(getSelectBuilder(), Arrays.asList(columns));
    }

}
