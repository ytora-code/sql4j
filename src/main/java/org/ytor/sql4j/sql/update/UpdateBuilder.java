package org.ytor.sql4j.sql.update;

import org.ytor.sql4j.core.SQLHelper;
import org.ytor.sql4j.sql.SqlBuilder;
import org.ytor.sql4j.translate.ITranslator;

/**
 * UPDATE 构造器
 */
public class UpdateBuilder extends SqlBuilder {

    /**
     * UPDATE 阶段，指定表
     */
    private UpdateStage updateStage;

    /**
     * SET 阶段，指定更新的字段
     */
    private SetStage setStage;

    /**
     * WHERE 阶段，指定条件
     */
    private UpdateWhereStage whereStage;

    public UpdateBuilder(SQLHelper sqlHelper) {
        this.sqlHelper = sqlHelper;
    }

    public SQLHelper getSQLHelper() {
        return sqlHelper;
    }

    @Override
    public ITranslator getTranslator() {
        return sqlHelper.getTranslator();
    }

    public void setUpdateStage(UpdateStage updateStage) {
        this.updateStage = updateStage;
    }

    public UpdateStage getUpdateStage() {
        return updateStage;
    }

    public void setSetStage(SetStage setStage) {
        this.setStage = setStage;
    }

    public SetStage getSetStage() {
        return setStage;
    }

    public void setWhereStage(UpdateWhereStage whereStage) {
        this.whereStage = whereStage;
    }

    public UpdateWhereStage getWhereStage() {
        return whereStage;
    }
}
