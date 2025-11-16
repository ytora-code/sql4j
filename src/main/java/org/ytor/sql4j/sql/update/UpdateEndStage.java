package org.ytor.sql4j.sql.update;

/**
 * 结束阶段
 */
public interface UpdateEndStage {

    /**
     * 修改影响行数
     */
    Integer submit();

}
