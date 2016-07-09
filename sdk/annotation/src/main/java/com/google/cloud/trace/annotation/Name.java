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
 * Annotation type used to set the name of a span or span label.
 *
 * <p>If this annotation appears on a method marked with {@link Span}, the value of this annotation
 * will be used for the method's span's name. If this annotation is used on a parameter, it will
 * serve as the parameter's label's name, if a span label is generated for the parameter.
 *
 * <p>The use of this and the associated annotations requires the use of an appropriate support
 * library that processes them.
 *
 * @see Label
 * @see Span
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Name {
  /**
   * Returns the name for a span or span label.
   *
   * @return the name for a span or span label.
   */
  String value();
}
