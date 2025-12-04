package xyz.ytora;

import org.junit.jupiter.api.Test;
import xyz.ytora.bean.Bean;

/**
 * created by YT on 2025/12/4 19:35:55
 * <br/>
 */
public class OrmTest {
    @Test
    public void testBean() {
        Bean bean = new Bean();
        bean.insert();
    }
}
