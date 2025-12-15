package xyz.ytora.sql4j.sql.select;

import xyz.ytora.sql4j.sql.SqlInfo;

import java.util.List;
import java.util.Map;

/**
 * 结束阶段
 */
public interface SelectEndStage {

    SqlInfo end();

    <T> List<T> submit(Class<T> clazz);

    List<Map<String, Object>> submit();
}
