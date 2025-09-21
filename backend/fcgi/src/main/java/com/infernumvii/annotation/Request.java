package com.infernumvii.annotation;
import java.lang.annotation.*;

import com.infernumvii.annotation.model.Method;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Request {
    Method method() default Method.ANY;
    String uri() default "*";
}
