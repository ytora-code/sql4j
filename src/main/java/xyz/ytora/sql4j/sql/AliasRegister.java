package xyz.ytora.sql4j.sql;

import xyz.ytora.sql4j.sql.select.AbsSelect;
import xyz.ytora.sql4j.util.StrUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表别名注册器
 */
public class AliasRegister {

    /**
     * 映射：实体类 -> 别名
     */
    private final Map<Class<?>, String> classAliasMapper = new HashMap<>();

    /**
     * 将子查询作为虚拟表时，子查询也需要别名
     * 映射：子查询 -> 别名
     */
    private final Map<AbsSelect, String> subSelectAliasMapper = new HashMap<>();

    public Boolean single() {
        return classAliasMapper.size() <= 1;
    }

    public String getAlias(Class<?> clazz) {
        return classAliasMapper.get(clazz);
    }

    public String getAlias(AbsSelect subSelect) {
        return subSelectAliasMapper.get(subSelect);
    }

    public String addAlias(Class<?> clazz) {
        String tableName = StrUtil.toLowerUnderline(clazz.getSimpleName());
        String alias = Arrays.stream(tableName.split("_")).map(i -> {
            if (!i.isEmpty()) {
                return i.substring(0, 1).toLowerCase();
            } else {
                return "";
            }
        }).collect(Collectors.joining());
        return addAlias(clazz, alias);
    }

    public String addAlias(AbsSelect subSelect) {
        String alise = numberToLetter(subSelectAliasMapper.size());
        subSelectAliasMapper.put(subSelect, alise);
        return alise;
    }

    public String addAlias(Class<?> clazz, String alise) {
        // 校验别名是否重复
        Collection<String> values = classAliasMapper.values();
        boolean exist = values.contains(alise);
        String _alise = alise;
        for (int i = 1; exist; i++) {
            alise = _alise + i;
            exist = values.contains(alise);
        }

        classAliasMapper.put(clazz, alise);
        return alise;
    }

    private String numberToLetter(int num) {
        // 校验输入：数字必须大于等于0
        if (num < 0) {
            throw new IllegalArgumentException("输入数字必须大于等于0");
        }

        StringBuilder sb = new StringBuilder();
        // 特殊处理 num=0 的情况（避免循环不执行，直接返回"a"）
        if (num == 0) {
            return "a";
        }

        while (num > 0) {
            // 直接取余得到 0-25 的索引（对应 a-z），无需前置减1
            int remainder = num % 26;
            // 转换为字母并逆序追加
            sb.append((char) ('a' + remainder));
            // 整除26计算下一位
            num = num / 26;
        }

        // 反转得到正确顺序
        return sb.reverse().toString();
    }

}
