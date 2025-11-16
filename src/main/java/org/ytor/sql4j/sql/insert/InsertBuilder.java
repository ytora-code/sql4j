package org.ytor.sql4j.sql.insert;

import org.ytor.sql4j.core.SQLHelper;
import org.ytor.sql4j.sql.SqlBuilder;
import org.ytor.sql4j.translate.ITranslator;

/**
 * INSERT 构造器
 */
public class InsertBuilder extends SqlBuilder {

    /**
     * INSERT 阶段，指定表
     */
    private InsertStage insertStage;

    /**
     * INTO 阶段，指定字段
     */
    private IntoStage intoStage;

    /**
     * VALUES 阶段，指定数据
     */
    private ValuesStage valuesStage;

    public InsertBuilder(SQLHelper sqlHelper) {
        this.sqlHelper = sqlHelper;
    }

    public SQLHelper getSQLHelper() {
        return sqlHelper;
    }

    @Override
    public ITranslator getTranslator() {
        return sqlHelper.getTranslator();
    }

    public void setInsertStage(InsertStage insertStage) {
        this.insertStage = insertStage;
    }

    public InsertStage getInsertStage() {
        return insertStage;
    }

    public void setIntoStage(IntoStage intoStage) {
        this.intoStage = intoStage;
    }

    public IntoStage getIntoStage() {
        return intoStage;
    }

    public void setValuesStage(ValuesStage valuesStage) {
        this.valuesStage = valuesStage;
    }

    public ValuesStage getValuesStage() {
        return valuesStage;
    }
}
