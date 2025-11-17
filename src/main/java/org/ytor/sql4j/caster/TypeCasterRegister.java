package org.ytor.sql4j.caster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类型映射注册
 */
public class TypeCasterRegister {

    private final Map<TypePair, Caster<?, ?>> typeCasterPairs = new ConcurrentHashMap<>();

    /**
     * 注册类型转换器
     * @param pair 原始类型和目标类型对
     * @param caster 原始类型转换为目标类型的转换逻辑
     */
    public void register(TypePair pair, Caster<?, ?> caster) {
        typeCasterPairs.put(pair, caster);
    }

    /**
     * 根据 原始类型和目标类型对 获取其转换逻辑
     * @param pair 原始类型和目标类型对
     * @return 转换逻辑
     */
    public <S, T> Caster<S, T> getCaster(TypePair pair) {
        return (Caster<S, T>) typeCasterPairs.get(pair);
    }

}
