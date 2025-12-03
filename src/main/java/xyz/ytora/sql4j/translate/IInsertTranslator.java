package xyz.ytora.sql4j.translate;

import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.insert.InsertBuilder;

/**
 * INSERT 翻译器
 */
public interface IInsertTranslator {

    SqlInfo translate(InsertBuilder builder);
}
