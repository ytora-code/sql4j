package org.ytor.sql4j.sql.select;

import org.ytor.sql4j.sql.AbsSql;
import org.ytor.sql4j.sql.SqlInfo;

/**
 * SELECT 阶段的抽象基类
 */
public abstract class AbsSelect extends AbsSql {

    public SelectBuilder getSelectBuilder() {
        return (SelectBuilder) register;
    }

    protected void setSelectBuilder(SelectBuilder selectBuilder) {
        this.register = selectBuilder;
    }

}
