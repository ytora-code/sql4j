package xyz.ytora.bean;

import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.orm.Entity;

/**
 * created by YT on 2025/12/4 19:36:20
 * <br/>
 */
public class Bean extends Entity<Bean> {

    private String name;

    @Column("my_age")
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
