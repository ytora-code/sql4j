package xyz.yangtong.sql4j.sql.update;

import xyz.yangtong.sql4j.sql.AbsSql;

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
