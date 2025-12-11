package xyz.ytora.sql4j.sql.delete;

import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.util.Sql4jUtil;

import java.util.function.Consumer;

/**
 * FROM 阶段，指定要删除的目标表
 */
public class FromStage extends AbsDelete implements DeleteEndStage {

    /**
     * 表类型：1-物理表(class实体类) / 2-物理表(字符串直接指定表名称)
     */
    private Integer tableType;

    private Class<?> table;

    private String tableStr;

    public FromStage(DeleteBuilder deleteBuilder, Class<?> table) {
        setDeleteBuilder(deleteBuilder);
        getDeleteBuilder().setFromStage(this);
        getDeleteBuilder().addAlias(table);
        this.table = table;
        this.tableType = 1;
    }

    public FromStage(DeleteBuilder deleteBuilder, String tableStr) {
        setDeleteBuilder(deleteBuilder);
        getDeleteBuilder().setFromStage(this);
        this.tableStr = tableStr;
        this.tableType = 2;
    }

    /**
     * FROM 后面一定是 WHERE 阶段
     */
    public DeleteWhereStage where(Consumer<ConditionExpressionBuilder> where) {
        return new DeleteWhereStage(getDeleteBuilder(), where);
    }

    /**
     * FROM 后可能结束
     */
    public SqlInfo end() {
        return getDeleteBuilder().getTranslator().translate(getDeleteBuilder());
    }

    /**
     * 获取要被删除数据的目标表名称
     */
    public String getTableName() {
        if (tableType == 1) {
            return Sql4jUtil.parseTableNameFromClass(table);
        }
        return tableStr;
    }

    @Override
    public Integer submit() {
        return getDeleteBuilder().getSQLHelper().getSqlExecutionEngine().executeDelete(getDeleteBuilder().getTranslator().translate(getDeleteBuilder())).getEffectedRows();
    }
}
