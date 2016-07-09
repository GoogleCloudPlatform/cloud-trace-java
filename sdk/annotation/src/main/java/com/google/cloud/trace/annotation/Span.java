// Copyright 2016 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation type used to create a trace span for a method. The span will be created as a child of
 * the current span.
 *
 * <p>The use of this and the associated annotations requires the use of an appropriate support
 * library that processes them.
 *
 * @see Label
 * @see Name
 * @see Option
 * @see Span
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Span {
  /**
   * Indicates whether to generate a span for the annotated method.
   *
   * @return whether to generate a span for the annotated method.
   */
  Option trace() default Option.DEFAULT;

  /**
   * Indicates whether to add a stack trace label to the annotated method's span.
   *
   * @return whether to add a stack trace label to the annotated method's span.
   */
  Option stackTrace() default Option.DEFAULT;

  /**
   * Indicates whether to add call labels to the annotated method's span. Call labels identify the
   * method, class, and package names of the method.
   *
   * @return whether to add call labels to the annotated method's span.
   */
  Option callLabels() default Option.DEFAULT;

  /**
   * Returns the names of labelers used to label the annotated method's span. The availability of
   * functionality of labelers is determined by the annotation processor used to process this and
   * associated annotations.
   *
   * @return the names of labelers used to label the annotated method's span.
   */
  String[] labels() default {};

  /**
   * Indicates whether the annotated method serves as the entry point of the application. Additional
   * labels will be generated for the entry span.
   *
   * @return whether the annotated method serves as the entry point of the application.
   */
  boolean entry() default false;

  /**
   * Returns the label key prefix for the annotated method's parameters.
   *
   * @return the label key prefix for the annotated method's parameters.
   */
  String labelPrefix() default "/";

  /**
   * Determines whether to add labels for all of the annotated method's parameters.
   *
   * @return whether to add labels for all of the annotated method's parameters.
   */
  boolean labelAll() default false;
}
