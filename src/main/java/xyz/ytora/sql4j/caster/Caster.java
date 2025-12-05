package xyz.ytora.sql4j.caster;

/**
 * 类型转换
 */
public interface Caster<S, T> {

    /**
     * 将原始值转为目标类型
     * @param sourceVal 原始值
     * @return 目标类型的值
     */
    T cast(S sourceVal);

    /**
     * 获取原始类型
     */
    Class<S>  getSourceType();

    /**
     * 获取目标类型
     */
    Class<T> getTargetType();
}
