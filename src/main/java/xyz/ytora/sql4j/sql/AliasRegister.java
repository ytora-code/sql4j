package xyz.ytora.sql4j.sql;

import xyz.ytora.sql4j.sql.select.TableInfo;
import xyz.ytora.ytool.str.Strs;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表别名注册器
 */
public class AliasRegister {

    /**
     * 表和别名的映射
     * tableAliasMapper 的 key 可以是：
     * 1.实体类class
     * 2.字符串
     * 3。子查询 AbsSelect
     */
    private final Map<TableInfo, String> tableAliasMapper = new LinkedHashMap<>();

    /**
     * 判断是否单表，如果单表就不用别名了
     */
    public Boolean single() {
        return tableAliasMapper.size() <= 1;
    }

    /**
     * 根据表信息获取表的别名
     */
    public String getAlias(TableInfo tableInfo) {
        for (Object key : tableAliasMapper.keySet()) {
            if (key.equals(tableInfo)) {
                return tableAliasMapper.get(key);
            }
        }
        return null;
    }

    /**
     * 根据表的实体类获取表的别名
     */
    public String getAlias(Class<?> tableCls) {
        for (TableInfo key : tableAliasMapper.keySet()) {
            if (key.tableCls().equals(tableCls)) {
                return tableAliasMapper.get(key);
            }
        }
        return null;
    }

    /**
     * 注册表的别名
     */
    public String addAlias(TableInfo tableInfo) {
        Integer tableType = tableInfo.tableType();
        if (tableType == 1) {
            Class<?> clazz = tableInfo.tableCls();
            String tableName = Strs.toUnderline(clazz.getSimpleName());
            String alias = Arrays.stream(tableName.split("_")).map(i -> i.isEmpty() ? "" : i.substring(0, 1).toLowerCase()).collect(Collectors.joining());
            return addAlias(tableInfo, alias);
        } else if (tableType == 2) {
            String alias = Arrays.stream(tableInfo.tableStr().split("_")).map(i -> i.isEmpty() ? "" : i.substring(0, 1).toLowerCase()).collect(Collectors.joining());
            return addAlias(tableInfo, alias);
        } else {
            String alise = numberToLetter(tableAliasMapper.size());
            return addAlias(tableInfo, alise);
        }
    }

    /**
     * 添加别名
     */
    public String addAlias(TableInfo tableInfo, String alise) {
        // 校验别名是否重复
        Collection<String> values = tableAliasMapper.values();
        boolean exist = values.contains(alise);
        String _alise = alise;
        for (int i = 1; exist; i++) {
            alise = _alise + i;
            exist = values.contains(alise);
        }

        tableAliasMapper.put(tableInfo, alise);
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
