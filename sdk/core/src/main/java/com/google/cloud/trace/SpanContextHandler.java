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

package com.google.cloud.trace;

import com.google.cloud.trace.core.SpanContext;

/**
 * An interface for managing span contexts.
 *
 * <p>The interface supports attaching and detaching contexts. When a context is attached, the
 * previous context is returned. To detach a context, pass in the previous context.
 */
public interface SpanContextHandler {
  /**
   * Returns the current span context.
   *
   * @return the current span context.
   */
  SpanContext current();

  /**
   * Replace the current SpanContext with a new SpanContext.
   * @param context the {@link SpanContext} to attach
   * @return The previous SpanContext
   */
  SpanContext attach(SpanContext context);

  /**
   * Replace the current SpanContext with a previous SpanContext.
   * @param toAttach the previous SpanContext that should be re-attached.
   */
  void detach(SpanContext toAttach);
}
