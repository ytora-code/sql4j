package xyz.ytora.sql4j;

import org.junit.jupiter.api.Test;
import xyz.ytora.sql4j.bean.Bean;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.func.support.Raw;

import java.util.List;

/**
 * created by YT on 2025/12/4 19:35:55
 * <br/>
 */
public class OrmTest {
    @Test
    public void testBean() {
        SQLHelper sqlHelper = new SQLHelper();
        Bean bean = new Bean();
//        bean.insert();

        List<Object> beans1 = sqlHelper.select(Raw.of("count(1)")).from(Bean.class).submit(Object.class);
        System.out.println(beans1);
    }
}
