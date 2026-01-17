package xyz.ytora.sql4j.meta;

import xyz.ytora.sql4j.enums.DbType;
import xyz.ytora.sql4j.meta.model.*;

import java.sql.Connection;
import java.util.List;

/**
 * 获取表的元数据
 */
public interface IMetaService {

    /**
     * 推断数据库类型
     */
    DbType inferDbType();

    /**
     * 推断数据库类型
     */
    DbType inferDbType(Connection connection);

    /**
     * 获取所有数据库名称，MySQL中 catalog 和 schema 是等价的
     */
    List<String> listCatalogs();

    /**
     * 获取指定库下的所有模式名称，MySQL调用该方法获取的是空数组
     */
    List<String> listSchemas(String catalog);

    /**
     * 获取指定库的指定模式下面表元数据
     */
    List<TableMeta> listTables(String catalog, String schema, String table);

    /**
     * 获取指定库的指定模式下面视图元数据
     */
    List<ViewMeta> listViews(String catalog, String schema, String table);

    /**
     * 获取指定库的指定模式下面函数元数据
     */
    List<FunctionMeta> listFunctions(String catalog, String schema, String function);

    /**
     * 获取指定库的指定模式下面存储过程元数据
     */
    List<ProcedureMeta> listProcedures(String catalog, String schema, String procedure);

    /**
     * 获取指定库的指定模式下面序列元数据
     */
    List<SequenceMeta> listSequences(String catalog, String schema, String sequence);

    /**
     * 获取指定表的所有列消息
     */
    List<ColumnMeta> listColumns(String catalog, String schema, String tableName);

    /**
     * 获取指定表的所有主键
     */
    List<String> listPrimaryKeys(String catalog, String schema, String tableName);
}
