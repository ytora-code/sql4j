//package org.ytor.sql4j.sql.ddl;
//
//import org.ytor.sql4j.core.SQLHelper;
//import org.ytor.sql4j.sql.SqlBuilder;
//import org.ytor.sql4j.translate.ITranslator;
//
///**
// * DDL 构造器
// */
//public class DDLBuilder extends SqlBuilder {
//
//    private DDLStage ddlStage;
//
//    public DDLBuilder(SQLHelper sqlHelper) {
//        this.sqlHelper = sqlHelper;
//    }
//
//    @Override
//    protected ITranslator getTranslator() {
//        return null;
//    }
//
//    @Override
//    public Boolean getIsSub() {
//        return false;
//    }
//
//    @Override
//    public void isSub() {
//        throw new UnsupportedOperationException("DDL 不支持设置子查询");
//    }
//
//    public void setDdlStage(DDLStage ddlStage) {
//        this.ddlStage = ddlStage;
//    }
//
//    public DDLStage getDdlStage() {
//        return ddlStage;
//    }
//}
