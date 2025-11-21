package xyz.yangtong.sql4j.caster;

/**
 * 类型转换
 */
@FunctionalInterface
public interface Caster<S, T> {

    /**
     * 将原始值转为目标类型
     * @param sourceVal 原始值
     * @param targetType 目标类型
     * @return 目标值
     */
    T cast(S sourceVal, Class<T> targetType);

}
