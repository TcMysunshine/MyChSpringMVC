package com.nju.chenhao.ChSpring.Annotation;

import java.lang.annotation.*;
//作用于类和方法
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public  @interface ChRequestMapping {
    String value() default "";
}
