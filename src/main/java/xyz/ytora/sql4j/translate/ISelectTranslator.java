package xyz.ytora.sql4j.translate;

import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.select.SelectBuilder;

/**
 * SELECT 翻译器
 */
public interface ISelectTranslator {

    SqlInfo translate(SelectBuilder builder);
}
