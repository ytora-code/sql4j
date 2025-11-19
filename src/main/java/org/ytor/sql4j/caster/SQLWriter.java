package org.ytor.sql4j.caster;

/**
 * 类型转换：往数据库写出数据时
 */
@FunctionalInterface
public interface SQLWriter {

    /**
     * 某个字段要自定义数据写出逻辑，需要重写该方法，最后进入数据库的数据就是方法返回值
     */
    Object write();

}
