package xyz.ytora.sql4j.caster;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 类型
 */
public class TypePair {

    /**
     * 原始类型
     */
    private final Type source;

    /**
     * 目标类型
     */
    private final Type target;

    public TypePair(Type source, Type target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypePair typePair = (TypePair) o;
        return Objects.equals(source, typePair.source)
                && Objects.equals(target, typePair.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }

}
