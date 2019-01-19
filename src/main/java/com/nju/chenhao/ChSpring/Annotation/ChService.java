package com.nju.chenhao.ChSpring.Annotation;

import java.lang.annotation.*;
//作用于类
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChService {
    String value() default "";
}

