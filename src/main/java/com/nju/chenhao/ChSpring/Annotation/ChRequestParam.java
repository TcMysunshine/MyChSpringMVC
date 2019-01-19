package com.nju.chenhao.ChSpring.Annotation;

import java.lang.annotation.*;
//作用于参数
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChRequestParam {
    String value() default "";
}
