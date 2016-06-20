package com.google.cloud.trace.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Span {
  Option trace() default Option.DEFAULT;
  Option stackTrace() default Option.DEFAULT;
  Option callLabels() default Option.DEFAULT;
  String[] labels() default {};
  boolean entry() default false;
  String labelPrefix() default "/";
  boolean labelAll() default false;
}
