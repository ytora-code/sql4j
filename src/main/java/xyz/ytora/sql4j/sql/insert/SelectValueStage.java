package xyz.ytora.sql4j.sql.insert;

import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.select.AbsSelect;

import java.util.List;

/**
 * INSERT INTO sys_user select * from sys_user1
 */
public class SelectValueStage extends AbsInsert implements InsertEndStage {

    /**
     * 将 subSelect 的查询结果集作为新增的数据
     */
    private final AbsSelect subSelect;

    public SelectValueStage(InsertBuilder insertBuilder, AbsSelect subSelect) {
        setInsertBuilder(insertBuilder);
        getInsertBuilder().setSelectValueStage(this);
        this.subSelect = subSelect;
    }

    /**
     * VALUES 后可能结束
     */
    public SqlInfo end() {
        return getInsertBuilder().getTranslator().translate(getInsertBuilder());
    }

    public AbsSelect getSubSelect() {
        return subSelect;
    }

    @Override
    public List<Object> submit() {
        return getInsertBuilder().getSQLHelper().getSqlExecutionEngine().executeInsert(getInsertBuilder().getTranslator().translate(getInsertBuilder())).getIds();
    }
}
