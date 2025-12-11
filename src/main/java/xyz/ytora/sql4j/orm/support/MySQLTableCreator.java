package xyz.ytora.sql4j.orm.support;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.anno.Table;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.enums.ColumnType;
import xyz.ytora.sql4j.enums.DbType;
import xyz.ytora.sql4j.enums.IdType;
import xyz.ytora.sql4j.enums.PostgreSQLColumnType;
import xyz.ytora.sql4j.orm.ITableCreator;
import xyz.ytora.sql4j.util.Sql4jUtil;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.str.Strs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * MYSQL 数据库
 */
public class MySQLTableCreator implements ITableCreator {

    private final String CHECK_SQL = """
            SELECT COUNT(*) FROM information_schema.tables
            WHERE table_schema = '{}' AND table_name = '{}';
            """;

    @Override
    public DbType getDbType() {
        return DbType.MYSQL;
    }

    @Override
    public <T> boolean exist(Connection connection, String tableName) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        if (sqlHelper == null) {
            return false;
        }

        try {
            String schema = connection.getCatalog();
            String sql = Strs.format(CHECK_SQL, schema, tableName);
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new Sql4JException(e);
        }


        return false;
    }

    @Override
    public String toDDL(Class<?> entity, Connection connection) {
        Table tableAnno = entity.getAnnotation(Table.class);
        // 走到这，tableAnno肯定不为空
        String tableName;
        if (Strs.isNotEmpty(tableAnno.value())) {
            tableName = tableAnno.value();
        } else {
            tableName = Strs.toUnderline(entity.getSimpleName());
        }

        // 获取主键类型
        IdType idType = tableAnno.idType();
        String primaryKeyType = getPrimaryKeyType(idType);

        // 构建 SQL 语句
        StringBuilder createTableSQL = new StringBuilder();
        createTableSQL.append("\nCREATE TABLE ").append(tableName).append(" (\n\t");

        // 处理主键字段
        createTableSQL.append("id ")
                .append(primaryKeyType)
                .append(" PRIMARY KEY")
                .append(" COMMENT '").append("主键ID").append("'");

        // 通过 getter 方法来确定数据库字段类型
        List<MethodMetadata> getters = Sql4jUtil.getter(entity);
        for (MethodMetadata getter : getters) {
            FieldMetadata fieldMetadata = getter.toField();
            String name = fieldMetadata.getName();
            // 跳过 id 字段
            if ("id".equals(name)) {
                continue;
            }
            // 获取字段注解信息（如字段注释等）
            Column columnAnno = fieldMetadata.getAnnotation(Column.class);

            // 字段类型
            String columnType;
            if (columnAnno != null && columnAnno.type() != ColumnType.NONE) {
                ColumnType type = columnAnno.type();
                columnType = PostgreSQLColumnType.getColumnTypeName(type.name());
            } else {
                String columnTypeName = ColumnType.getColumnTypeName(fieldMetadata.getType());
                columnType = PostgreSQLColumnType.getColumnTypeName(columnTypeName);
            }

            // 字段名称
            String columnName;
            if (columnAnno != null && Strs.isNotEmpty(columnAnno.value())) {
                columnName = columnAnno.value();
            } else {
                columnName = Strs.toUnderline(name);
            }

            // 字段注释
            String comment = null;
            if (columnAnno != null && Strs.isNotEmpty(columnAnno.comment())) {
                comment = columnAnno.comment();
            }

            createTableSQL.append(",\n\t").append(columnName).append(" ").append(columnType);

            // 如果字段有注释
            if (comment != null) {
                createTableSQL.append(" COMMENT '").append(comment).append("'");
            }
        }

        // 如果表注解有注释，添加表注释
        String tableComment = tableAnno.comment();
        if (Strs.isNotEmpty(tableComment)) {
            createTableSQL.append("\n) COMMENT '").append(tableComment).append("';");
        } else {
            createTableSQL.append("\n);");
        }

        return createTableSQL.toString();
    }

    // 根据主键类型获取对应的数据库字段类型
    private String getPrimaryKeyType(IdType idType) {
        return switch (idType) {
            case AUTO_INCREMENT -> "INT AUTO_INCREMENT";
            case SNOWFLAKE -> "BIGINT";
            case UUID -> "VARCHAR(36)";
            default -> "VARCHAR(255)";
        };
    }

    // 根据字段类型获取对应的数据库字段类型
    private String getColumnType(Class<?> fieldType) {
        if (fieldType == Byte.class || fieldType == byte.class) {
            return "TINYINT";
        } else if (fieldType == Short.class || fieldType == short.class) {
            return "SMALLINT";
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return "INT";
        } else if (fieldType == Long.class || fieldType == long.class) {
            return "BIGINT";
        } else if (fieldType == Float.class || fieldType == float.class) {
            return "FLOAT";
        } else if (fieldType == Double.class || fieldType == double.class) {
            return "DOUBLE";
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return "TINYINT(1)";
        } else if (fieldType == String.class) {
            return "VARCHAR(255)";
        } else if (fieldType == byte[].class) {
            return "BLOB";
        } else if (fieldType == java.util.Date.class || fieldType == java.sql.Date.class) {
            return "DATE";
        } else if (fieldType == java.time.LocalDate.class) {
            return "DATE";
        } else if (fieldType == java.time.LocalDateTime.class) {
            return "DATETIME";
        } else if (fieldType.isEnum()) {
            return "VARCHAR(255)";
        } else {
            return "TEXT";  // 默认使用 TEXT 类型
        }
    }
}
