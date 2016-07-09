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
 * Annotation type used to mark a method parameter that should be added as a label to the current
 * span.
 *
 * <p>The generated label's key will be of the form {@code prefix[/name]}. The {@code prefix} will
 * be taken from the parameter's method's {@link Span#labelPrefix}, if specified, otherwise it will
 * be generated based on the method's name. This key will be overriden by the value of the {@link
 * Name} annotation, if present.
 *
 * <p>The generated label's value will be the return value of a
 * call to the parameter's {@link java.lang.Object#toString()} method.
 *
 * <p>The use of this and the associated annotations requires the use of an appropriate support
 * library that processes them.
 *
 * @see Name
 * @see Span
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Label {
  /**
   * Returns the name portion of the label key.
   *
   * @return the name portion of the label key. The name suffix is omitted if blank.
   */
  String name() default "/";

  /**
   * Determines whether to add this label to the current span.
   *
   * @return whether to add this label to the current span.
   */
  boolean enabled() default true;
}
