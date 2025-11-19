package org.ytor.sql4j.sql.delete;

import org.ytor.sql4j.sql.AbsSql;

/**
 * DELETE 阶段的抽象基类
 */
public abstract class AbsDelete extends AbsSql {

    protected DeleteBuilder getDeleteBuilder() {
        return (DeleteBuilder) register;
    }

    protected void setDeleteBuilder(DeleteBuilder deleteBuilder) {
        this.register = deleteBuilder;
    }

}
