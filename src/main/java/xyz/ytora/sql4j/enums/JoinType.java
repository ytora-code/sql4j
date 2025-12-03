package xyz.ytora.sql4j.enums;

/**
 * 连接类型
 */
public enum JoinType {
    LEFT_JOIN("LEFT JOIN"),
    RIGHT_JOIN("RIGHT JOIN"),
    INNER_JOIN("INNER JOIN");

    final String joinKey;

    JoinType(String joinKey) {
        this.joinKey = joinKey;
    }

    public String getJoinKey() {
        return joinKey;
    }
}
