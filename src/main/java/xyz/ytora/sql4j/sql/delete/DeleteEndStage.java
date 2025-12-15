package xyz.ytora.sql4j.sql.delete;

import xyz.ytora.sql4j.sql.SqlInfo;

/**
 * 结束阶段
 */
public interface DeleteEndStage {

    SqlInfo end();

    /**
     * 删除影响行数
     */
    Integer submit();

}
