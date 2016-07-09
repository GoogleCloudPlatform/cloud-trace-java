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
 * @param trace       an option that indicates whether to generate a span for the annotated method.
 * @param stackTrace  an option that indicates whether to add a stack trace label to the span.
 * @param callLabels  an option used to indicate whether to add call labels to the span. Call labels
 *                    identify the method, class, and package of the method.
 * @param labels      a string array containing the names of labelers used to label this span. The
 *                    availability of functionality of labelers is determined by the annotation
 *                    processor used to processor this and associated annotations.
 * @param entry       a boolean that indicates whether the annotation method serves as the entry
 *                    point of the application. Additional labels will be generated for the entry
 *                    span.
 * @param labelPrefix a string used as the label key prefix for this method's parameters.
 * @param labelAll    a boolean that indicates whether to add labels for all of this method's
 *                    parameters.
 * @see Label
 * @see Name
 * @see Option
 * @see Span
 */
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
