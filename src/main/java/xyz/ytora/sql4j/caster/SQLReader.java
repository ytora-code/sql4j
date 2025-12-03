package xyz.ytora.sql4j.caster;

/**
 * 类型转换：从数据库读入数据时
 */
@FunctionalInterface
public interface SQLReader {

    /**
     * 某个字段要自定义数据的读入逻辑，需要重写该方法
     * @param sourceVal JDBC默认返回的原始数据
     */
     Object read(Object sourceVal);

}
