package xyz.ytora.sql4j.orm;

import java.io.Serial;
import java.io.Serializable;

/**
 * 抽象实体类
 */
public class Entity<T extends Entity<T>> implements Serializable {

    @Serial
    private static final long serialVersionUID = 114514L;

    /**
     * 主键id
     */
    protected String id;

    public String getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public T setId(String id) {
        this.id = id;
        return (T) this;
    }

}
