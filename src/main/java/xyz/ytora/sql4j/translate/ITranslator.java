package xyz.ytora.sql4j.translate;

import xyz.ytora.sql4j.sql.SqlBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;

/**
 * SQL 翻译器
 */
public interface ITranslator {

    SqlInfo translate(SqlBuilder sqlBuilder);

}
