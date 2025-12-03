package xyz.ytora.sql4j.sql.update;

import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.sql.SqlBuilder;
import xyz.ytora.sql4j.translate.ITranslator;

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

    @Override
    public ITranslator getTranslator() {
        return sqlHelper.getTranslator();
    }

    @Override
    public Boolean getIsSub() {
        return false;
    }

    @Override
    public void isSub() {
        throw new UnsupportedOperationException("UPDATE 不支持设置子查询");
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
