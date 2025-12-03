//package org.ytor.sql4j.sql.ddl;
//
///**
// * DDL 阶段
// */
//public class DDLStage extends AbsDDL {
//
//    private final String sql;
//
//    public DDLStage(DDLBuilder ddlBuilder, String sql) {
//        setDeleteBuilder(ddlBuilder);
//        getDDLBuilder().setDdlStage(this);
//        this.sql = sql;
//    }
//
//    public Integer submit() {
//        sql.
//        return getDDLBuilder().getSQLHelper().getSqlExecutionEngine().executeDelete(getDDLBuilder().getTranslator().translate(getDDLBuilder()));
//    }
//}
