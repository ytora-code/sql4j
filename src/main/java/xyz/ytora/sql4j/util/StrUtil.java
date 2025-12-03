package xyz.ytora.sql4j.util;

/**
 * 字符串工具类
 */
public class StrUtil {

    /**
     * 将字符串从驼峰式（CamelCase）转换为下划线命名法（snake_case）
     * 例如 userName -> user_name
     */
    public static String toLowerUnderline(String targetStr) {
        if (targetStr == null || targetStr.isEmpty()) {
            return targetStr;
        }

        StringBuilder result = new StringBuilder();
        char[] chars = targetStr.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (Character.isUpperCase(ch)) {
                // 前面不是开头，也不是连续大写，则插入分隔符
                if (i > 0 && (Character.isLowerCase(chars[i - 1]) ||
                        (i + 1 < chars.length && Character.isLowerCase(chars[i + 1])))) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * 将字符串从下划线命名法（snake_case）转换为小驼峰命名法（camelCase）
     * 例如 user_name -> userName
     */
    public static String toCamelCase(String targetStr) {
        if (targetStr == null || targetStr.isEmpty()) {
            return targetStr;
        }

        StringBuilder result = new StringBuilder();
        boolean toUpperCase = false;

        // 遍历每个字符
        for (char ch : targetStr.toCharArray()) {
            if (ch == '_') {
                // 遇到下划线，设置标志，下一字符转换为大写
                toUpperCase = true;
            } else {
                if (toUpperCase) {
                    result.append(Character.toUpperCase(ch)); // 转换为大写
                    toUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(ch)); // 保持小写
                }
            }
        }
        return result.toString();
    }

    /**
     * 将字符串从下划线命名法（snake_case）转换为大驼峰命名法（PascalCase）
     * 例如 user_name -> UserName
     */
    public static String toPascalCase(String targetStr) {
        String camelCaseStr = toCamelCase(targetStr);
        // 将首字母大写
        if (!camelCaseStr.isEmpty()) {
            return Character.toUpperCase(camelCaseStr.charAt(0)) + camelCaseStr.substring(1);
        }
        return camelCaseStr;
    }

}
