package xyz.ytora.sql4j.sql.delete;

import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.select.TableInfo;
import xyz.ytora.sql4j.util.Sql4jUtil;

import java.util.function.Consumer;

/**
 * FROM 阶段，指定要删除的目标表
 */
public class FromStage extends AbsDelete implements DeleteEndStage {

    private final TableInfo tableInfo;

    public FromStage(DeleteBuilder deleteBuilder, Class<?> table) {
        setDeleteBuilder(deleteBuilder);
        getDeleteBuilder().setFromStage(this);
        this.tableInfo = new TableInfo(1, table, null, null);
        getDeleteBuilder().addAlias(tableInfo);
    }

    public FromStage(DeleteBuilder deleteBuilder, String tableStr) {
        setDeleteBuilder(deleteBuilder);
        getDeleteBuilder().setFromStage(this);
        this.tableInfo = new TableInfo(1, null, tableStr, null);
        getDeleteBuilder().addAlias(tableInfo);
    }

    /**
     * FROM 后面一定是 WHERE 阶段
     */
    public DeleteWhereStage where(Consumer<ConditionExpressionBuilder> where) {
        return new DeleteWhereStage(getDeleteBuilder(), where);
    }

    public DeleteWhereStage where(ConditionExpressionBuilder where) {
        return new DeleteWhereStage(getDeleteBuilder(), where);
    }

    /**
     * 获取要被删除数据的目标表名称
     */
    public String getTableName() {
        if (tableInfo.tableType() == 1) {
            return Sql4jUtil.parseTableNameFromClass(tableInfo.tableCls());
        }
        return tableInfo.tableStr();
    }

    /**
     * 获取要被删除数据的目标表的CLASS
     */
    public Class<?> getTableClass() {
        return tableInfo.tableCls();
    }

    @Override
    public SqlInfo end() {
        return getDeleteBuilder().getTranslator().translate(getDeleteBuilder());
    }

    @Override
    public Integer submit() {
        return getDeleteBuilder().getSQLHelper().getSqlExecutionEngine().executeDelete(getDeleteBuilder().getTranslator().translate(getDeleteBuilder())).getEffectedRows();
    }
}
