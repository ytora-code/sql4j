package xyz.yangtong.sql4j.sql.insert;

import java.util.List;

/**
 * 结束阶段
 */
public interface InsertEndStage {

    /**
     * 新增数据的ID
     */
    public List<Object> submit();

}
