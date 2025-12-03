package xyz.ytora.sql4j.sql.delete;

/**
 * 结束阶段
 */
public interface DeleteEndStage {

    /**
     * 删除影响行数
     */
    Integer submit();

}
