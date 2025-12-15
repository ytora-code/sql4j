package xyz.ytora.sql4j.sql.update;

import xyz.ytora.sql4j.sql.SqlInfo;

/**
 * 结束阶段
 */
public interface UpdateEndStage {

    SqlInfo end();

    /**
     * 修改影响行数
     */
    Integer submit();

}
