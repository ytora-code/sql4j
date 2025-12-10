package xyz.ytora.sql4j.sql.select;

import java.util.List;
import java.util.Map;

/**
 * 结束阶段
 */
public interface SelectEndStage {

    <T> List<T> submit(Class<T> clazz);

    List<Map<String, Object>> submit();
}
