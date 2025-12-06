package xyz.ytora.sql4j.orm;

import xyz.ytora.sql4j.anno.Table;
import xyz.ytora.sql4j.core.IConnectionProvider;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.enums.DbType;
import xyz.ytora.sql4j.enums.SqlType;
import xyz.ytora.sql4j.orm.support.MySQLTableCreator;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.ytool.str.Strs;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库表创建器管理器
 */
public class TableCreatorManager {

    private final Map<DbType, ITableCreator> tableCreatorMap = new HashMap<>();

    public TableCreatorManager() {
        // 注册 MYSQL 的TableCreator
        registerTableCreator(new MySQLTableCreator());
    }

    public void registerTableCreator(ITableCreator tableCreator) {
        tableCreatorMap.put(tableCreator.getDbType(), tableCreator);
    }

    public <T> void createTableIfNotExist(SQLHelper sqlHelper, Class<?> clazz) {
        if (clazz == null || !AbsEntity.class.isAssignableFrom(clazz)) {
            return;
        }
        Table tableAnno = clazz.getAnnotation(Table.class);
        if (tableAnno == null || !tableAnno.createIfNotExist()) {
            return;
        }
        ITableCreator tableCreator = tableCreatorMap.get(tableAnno.dbType());
        if (tableCreator == null) {
            return;
        }

        IConnectionProvider connectionProvider = sqlHelper.getConnectionProvider();
        if (connectionProvider == null) {
            return;
        }

        Connection connection = connectionProvider.getConnection();
        try {
            String tableName;
            if (Strs.isNotEmpty(tableAnno.value())) {
                tableName = tableAnno.value();
            } else {
                tableName = Strs.toUnderline(clazz.getSimpleName());
            }
            if (!tableCreator.exist(connection, tableName)) {
                // 只有表不存在，才会进入这里
                String ddl = tableCreator.toDDL(clazz);
                sqlHelper.getSqlExecutionEngine().executeDDL(new SqlInfo(null, SqlType.DDL, ddl, null));
            }
        } finally {
            connectionProvider.closeConnection(connection);
        }

    }
}
