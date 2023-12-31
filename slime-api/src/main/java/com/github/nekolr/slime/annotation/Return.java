package com.github.nekolr.slime.annotation;

import java.lang.annotation.*;

/**
 * The following attributes are available:，The type of the return value of the page
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Return {
    Class<?>[] value();
}
