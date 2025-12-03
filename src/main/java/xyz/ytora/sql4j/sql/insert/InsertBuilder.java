package xyz.ytora.sql4j.sql.insert;

import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.sql.SqlBuilder;
import xyz.ytora.sql4j.translate.ITranslator;

/**
 * INSERT 构造器
 */
public class InsertBuilder extends SqlBuilder {

    private Boolean isSub = false;

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

    /**
     * SELECT VALUES 阶段，将查询结果集作为插入的数据，与 ValuesStage 阶段相斥
     */
    private SelectValueStage selectValueStage;

    public InsertBuilder(SQLHelper sqlHelper) {
        this.sqlHelper = sqlHelper;
    }

    @Override
    public ITranslator getTranslator() {
        return sqlHelper.getTranslator();
    }

    @Override
    public Boolean getIsSub() {
        return isSub;
    }

    @Override
    public void isSub() {
        this.isSub = true;
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

    public void setSelectValueStage(SelectValueStage selectValueStage) {
        this.selectValueStage = selectValueStage;
    }

    public SelectValueStage getSelectValueStage() {
        return selectValueStage;
    }
}
