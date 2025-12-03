package xyz.ytora.sql4j.sql.update;

import xyz.ytora.sql4j.sql.AbsSql;

/**
 * UPDATE 阶段的抽象基类
 */
public abstract class AbsUpdate extends AbsSql {

    protected UpdateBuilder getUpdateBuilder() {
        return (UpdateBuilder) register;
    }

    protected void setUpdateBuilder(UpdateBuilder UpdateBuilder) {
        this.register = UpdateBuilder;
    }

}
