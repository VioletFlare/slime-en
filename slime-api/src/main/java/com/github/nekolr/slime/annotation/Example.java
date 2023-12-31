package com.github.nekolr.slime.annotation;

import java.lang.annotation.*;

/**
 * The following attributes are available:，Code examples
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Example {
    String value();
}
