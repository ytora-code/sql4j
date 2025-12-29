package xyz.ytora.sql4j.orm.querygen.token;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 分词结果封装类
 */
public class QueryToken {
    /**
     * 匹配字段
     */
    private String key;
    /**
     * true: 使用"=", false: 使用"!="
     */
    private Boolean positive;
    /**
     * 匹配值
     */
    private String value;
    /**
     *
     * 字段类型
     */
    private Class<?> valueClass;

    public QueryToken(String key, boolean positive, String value) {
        this.key = key;
        this.positive = positive;
        this.value = URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public Boolean isPositive() {
        return positive;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getPositive() {
        return positive;
    }

    public void setPositive(Boolean positive) {
        this.positive = positive;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public void setValueClass(Class<?> valueClass) {
        this.valueClass = valueClass;
    }

    @Override
    public String toString() {
        return "(" + key + ", " + positive + ", " + value + ")";
    }
}