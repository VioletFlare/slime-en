package com.github.nekolr.slime.annotation;

import java.lang.annotation.*;

/**
 * The following attributes are available:，Used for code generation hints
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Comment {
    String value();
}
