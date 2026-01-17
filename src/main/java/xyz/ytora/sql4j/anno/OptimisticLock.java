package xyz.ytora.sql4j.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * created by YT on 2026/1/17 23:46:53
 * <br/>
 * 乐观锁
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface OptimisticLock {
}
