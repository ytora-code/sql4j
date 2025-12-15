package xyz.ytora.sql4j.sql.insert;

import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.List;

/**
 * 结束阶段
 */
public interface InsertEndStage {

    SqlInfo end();

    /**
     * 新增数据的ID
     */
    List<Object> submit();

}
