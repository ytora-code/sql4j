package xyz.ytora.sql4j.sql.update;

import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.orm.autofill.ColumnFiller;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.util.Sql4jUtil;
import xyz.ytora.ytool.str.Strs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * SET 阶段，指定要更新的字段和值
 */
public class SetStage extends AbsUpdate implements UpdateEndStage {

    /**
     * 映射：要修改的字段 -> 要修改的值
     */
    private final Map<SFunction<?, ?>, Object> updatedColumnValueMapper = new HashMap<>();

    public SetStage(UpdateBuilder updateBuilder) {
        setUpdateBuilder(updateBuilder);
        getUpdateBuilder().setSetStage(this);
    }

    /**
     * SET 后，可能继续 SET
     */
    public final <T> SetStage set(SFunction<T, ?> updatedColumn, Object value) {
        updatedColumnValueMapper.put(updatedColumn, value);
        return this;
    }

    /**
     * SET 后，可能进入 WHERE 阶段
     */
    public final UpdateWhereStage where(Consumer<ConditionExpressionBuilder> where) {
        return new UpdateWhereStage(getUpdateBuilder(), where);
    }

    public final UpdateWhereStage where(ConditionExpressionBuilder where) {
        return new UpdateWhereStage(getUpdateBuilder(), where);
    }

    public Map<SFunction<?, ?>, Object> getUpdatedColumnValueMapper() {
        // 计算自动填充的列
        Map<String, Class<? extends ColumnFiller>> mapper = Sql4jUtil.parseAutoFillColMapper(getUpdateBuilder().getUpdateStage().getTable());
        Set<String> autoFillColNameSet = mapper.keySet().stream().map(Strs::toUnderline).collect(Collectors.toSet());

        // 要修改的字段中包含填充字段
        for (SFunction<?, ?> col : updatedColumnValueMapper.keySet()) {
            String colName = Sql4jUtil.parseColumn(col, null);
            if (autoFillColNameSet.contains(colName)) {
                Object value = updatedColumnValueMapper.get(col);
                if (value == null) {
                    ColumnFiller columnFiller = Sql4jUtil.getAutoFiller(mapper.get(colName));
                    value = columnFiller.fillOnUpdate();
                    if (value != null) {
                        updatedColumnValueMapper.put(Raw.of(colName), value);
                    }
                }
                mapper.remove(colName);
            }
        }

        // 要修改的字段中没有的填充字段，也要进行处理
        if (!mapper.isEmpty()) {
            for (String colName : mapper.keySet()) {
                ColumnFiller columnFiller = Sql4jUtil.getAutoFiller(mapper.get(colName));
                Object value = columnFiller.fillOnUpdate();
                if (value != null) {
                    updatedColumnValueMapper.put(Raw.of(colName), value);
                }
            }
        }

        return updatedColumnValueMapper;
    }

    @Override
    public SqlInfo end() {
        return getUpdateBuilder().getTranslator().translate(getUpdateBuilder());
    }

    @Override
    public Integer submit() {
        return getUpdateBuilder().getSQLHelper().getSqlExecutionEngine().executeUpdate(getUpdateBuilder().getTranslator().translate(getUpdateBuilder())).getEffectedRows();
    }
}
