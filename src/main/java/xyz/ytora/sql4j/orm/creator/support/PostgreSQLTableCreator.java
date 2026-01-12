package xyz.ytora.sql4j.orm.creator.support;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.anno.Table;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.enums.ColumnType;
import xyz.ytora.sql4j.enums.DbType;
import xyz.ytora.sql4j.enums.IdType;
import xyz.ytora.sql4j.enums.PostgreSQLColumnType;
import xyz.ytora.sql4j.orm.creator.ITableCreator;
import xyz.ytora.sql4j.util.Sql4jUtil;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.str.Strs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PostgreSQL 数据库
 */
public class PostgreSQLTableCreator implements ITableCreator {

    private final String CHECK_SQL = """
            SELECT COUNT(*) FROM pg_catalog.pg_tables
            WHERE schemaname = '{}' AND tablename = '{}';
            """;

    @Override
    public DbType getDbType() {
        return DbType.POSTGRESQL;
    }

    @Override
    public <T> boolean exist(Connection connection, String tableName) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        if (sqlHelper == null) {
            return false;
        }

        try {
            // 获取当前数据库的 schema
            String schema = connection.getSchema();
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

        // 获取当前连接的 schema
        String schema;
        try {
            schema = connection.getSchema();
        } catch (SQLException e) {
            throw new Sql4JException(e);
        }
        if (Strs.isEmpty(schema)) {
            // 默认使用 public schema
            schema = "public";
        }

        // 构建 SQL 语句
        StringBuilder createTableSQL = new StringBuilder();
        createTableSQL.append("\nCREATE TABLE ").append(schema).append(".").append(tableName).append(" (\n\t");

        // 处理主键字段
        createTableSQL.append("id ")
                .append(primaryKeyType)
                .append(" PRIMARY KEY");

        // 通过 getter 方法来确定数据库字段类型
        List<MethodMetadata> getters = Sql4jUtil.getter(entity);
        Map<String, String> commentList = new LinkedHashMap<>();
        commentList.put("id", "主键ID");
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

            createTableSQL.append(",\n\t")
                    .append(columnName).append(" ").append(columnType);
            // 非空
            if (columnAnno != null && columnAnno.notNull()) {
                createTableSQL.append(" NOT NULL");
            }
            // 默认值
            if (columnAnno != null && Strs.isNotEmpty(columnAnno.defaultVal())) {
                createTableSQL.append(" DEFAULT ");
                // 如果字段类型是字符串，就要为默认值加单引号
                if (PostgreSQLColumnType.isStr(columnType)) {
                    createTableSQL.append("'").append(columnAnno.defaultVal()).append("'");
                } else {
                    createTableSQL.append(columnAnno.defaultVal());
                }
            }

            // 如果字段有注释
            if (comment != null) {
                commentList.put(columnName, comment);
            }
        }

        createTableSQL.append("\n);");

        // 如果表注解有注释，添加表注释
        String tableComment = tableAnno.comment();
        if (Strs.isNotEmpty(tableComment)) {
            createTableSQL.append("\n");
            createTableSQL.append(Strs.format("COMMENT ON TABLE {}.{} IS '{}';\n", schema, tableName, tableComment));
        }

        // 字段注释
        for (String column : commentList.keySet()) {
            String comment = commentList.get(column);
            createTableSQL.append(Strs.format("COMMENT ON COLUMN {}.{}.{} IS '{}';\n", schema, tableName, column, comment));
        }

        return createTableSQL.toString();
    }


    // 根据主键类型获取对应的数据库字段类型
    private String getPrimaryKeyType(IdType idType) {
        return switch (idType) {
            case AUTO_INCREMENT -> "SERIAL";
            case SNOWFLAKE -> "BIGINT";
            case UUID -> "UUID";
            default -> "VARCHAR(255)";
        };
    }

}
