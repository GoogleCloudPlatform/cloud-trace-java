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

package com.google.cloud.trace.core;

/**
 * An interface that represents a stack of {@link SpanContext}s.
 */
public interface TraceContextHandler {
  /**
   * Returns the current span context.
   *
   * @return the current span context.
   */
  SpanContext current();

  /**
   * Pushes a new span context onto the top of the stack.
   *
   * @param context a span context to push onto the stack.
   */
  void push(SpanContext context);

  /**
   * Pops the context off the top of the stack and returns it.
   *
   * @return the context on the top of the stack or null if the stack if empty.
   */
  SpanContext pop();
}
