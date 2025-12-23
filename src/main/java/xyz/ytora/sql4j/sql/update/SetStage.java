package xyz.ytora.sql4j.sql.update;

import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
