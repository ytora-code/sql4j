package org.ytor.sql4j.func;

import java.io.Serializable;

/**
 * 函数式接口，用于接收方法引用类型的参数
 */
@FunctionalInterface
public interface SFunction<T, R> extends Serializable {
    R apply(T t);
}
