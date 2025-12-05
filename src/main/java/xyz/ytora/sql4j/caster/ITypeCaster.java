package xyz.ytora.sql4j.caster;

import xyz.ytora.ytool.convert.TypePair;

/**
 * 读入数据的类型转换器接口
 */
public interface ITypeCaster {
    public <T, S> T cast(S sourceVal, Class<T> targetType);

    void register(TypePair pair, Caster<?, ?> caster);

    <S, T> Caster<S, T> getCaster(TypePair pair);
}
