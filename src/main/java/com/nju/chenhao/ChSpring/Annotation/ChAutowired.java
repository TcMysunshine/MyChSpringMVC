package com.nju.chenhao.ChSpring.Annotation;

import java.lang.annotation.*;

//修饰成员变量
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChAutowired {
    String value() default "";
}
