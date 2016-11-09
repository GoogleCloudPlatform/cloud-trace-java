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
 * Represents the current context of a Trace.
 */
public class TraceContext {
  private final SpanContext current, parent;

  public TraceContext(SpanContext current, SpanContext parent) {
    this.current = current;
    this.parent = parent;
  }

  /**
   * Returns the current {@link SpanContext}
   * @return the current {@link SpanContext}
   */
  public SpanContext getCurrent() {
    return current;
  }

  /**
   * Returns the parent of the current {@link SpanContext}
   * @return the parent of the current {@link SpanContext}
   */
  public SpanContext getParent() {
    return parent;
  }
}
