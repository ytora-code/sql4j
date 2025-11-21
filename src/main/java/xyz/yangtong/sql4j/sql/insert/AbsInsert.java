package xyz.yangtong.sql4j.sql.insert;

import xyz.yangtong.sql4j.sql.AbsSql;

/**
 * INSERT 阶段的抽象基类
 */
public abstract class AbsInsert extends AbsSql {

    protected InsertBuilder getInsertBuilder() {
        return (InsertBuilder) register;
    }

    protected void setInsertBuilder(InsertBuilder insertBuilder) {
        this.register = insertBuilder;
    }

}
