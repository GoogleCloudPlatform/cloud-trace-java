// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.sdk;

/**
 * Creates new {@link TraceContext} instances for use in {@link TraceSpanData}.
 */
public class DefaultTraceContextManager {

  private static final SpanIdGenerator spanIdGenerator = new SpanIdGenerator();

  /**
   * Gets the current trace context, which is null in the default implementation.
   */
  public TraceContext getCurrentTraceContext() {
    return null;
  }
  
  /**
   * Creates a new trace context for the given trace.
   */
  public TraceContext createTraceContext(String traceId, boolean shouldWrite) {
    return new TraceContext(traceId, spanIdGenerator.generate(), shouldWrite);
  }
}
