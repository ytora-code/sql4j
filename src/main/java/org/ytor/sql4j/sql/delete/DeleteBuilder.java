package org.ytor.sql4j.sql.delete;

import org.ytor.sql4j.core.SQLHelper;
import org.ytor.sql4j.sql.SqlBuilder;
import org.ytor.sql4j.translate.ITranslator;

/**
 * DELETE 构造器
 */
public class DeleteBuilder extends SqlBuilder {

    /**
     * DELETE 阶段
     */
    private DeleteStage deleteStage;

    /**
     * FROM 阶段，指定要删除的目标表
     */
    private FromStage fromStage;

    /**
     * WHERE 阶段，指定删除条件
     */
    private DeleteWhereStage whereStage;

    public DeleteBuilder(SQLHelper sqlHelper) {
        this.sqlHelper = sqlHelper;
    }

    @Override
    public ITranslator getTranslator() {
        return sqlHelper.getTranslator();
    }

    public void setDeleteStage(DeleteStage deleteStage) {
        this.deleteStage = deleteStage;
    }

    public DeleteStage getDeleteStage() {
        return deleteStage;
    }

    public void setFromStage(FromStage fromStage) {
        this.fromStage = fromStage;
    }

    public FromStage getFromStage() {
        return fromStage;
    }

    public void setWhereStage(DeleteWhereStage whereStage) {
        this.whereStage = whereStage;
    }

    public DeleteWhereStage getWhereStage() {
        return whereStage;
    }
}
