package xyz.ytora.sql4j.sql.select;

import java.util.List;

/**
 * 结束阶段
 */
public interface SelectEndStage {

    public <T> List<T> submit(Class<T> clazz);

}
