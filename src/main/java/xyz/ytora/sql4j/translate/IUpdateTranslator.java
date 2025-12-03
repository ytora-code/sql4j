package xyz.ytora.sql4j.translate;

import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.update.UpdateBuilder;

/**
 * UPDATE 翻译器
 */
public interface IUpdateTranslator {

    SqlInfo translate(UpdateBuilder builder);

}
