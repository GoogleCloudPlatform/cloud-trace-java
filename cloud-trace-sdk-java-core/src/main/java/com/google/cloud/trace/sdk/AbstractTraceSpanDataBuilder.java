// Copyright 2015 Google Inc. All rights reserved.
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

import java.math.BigInteger;
import java.util.Map;

/**
 * Base class useful for many {@link TraceSpanDataBuilder} implementations.
 */
public abstract class AbstractTraceSpanDataBuilder implements TraceSpanDataBuilder {
  public static SpanIdGenerator spanIdGenerator = new SpanIdGenerator();

  public static TraceIdGenerator traceIdGenerator = new UUIDTraceIdGenerator();

  @Override
  public Map<String, TraceSpanLabel> getLabelMap() {
    return null;
  }

  @Override
  public TraceSpanDataBuilder createChild(String childSpanName) {
    TraceContext parentContext = getTraceContext();
    TraceContext childContext = new TraceContext(
        parentContext.getTraceId(), spanIdGenerator.generate(), parentContext.getOptions());
    return createChildSpanDataBuilder(childContext, childSpanName, parentContext.getSpanId());
  }

  /**
   * Creates a new {@link TraceSpanDataBuilder} for a child span of this one.
   * Defaults to an instance of {@link DefaultTraceSpanDataBuilder}.
   * Override to customize. One example where this is useful is where the initial
   * span is from a servlet request -- child spans almost certainly would take
   * some other form.
   */
  protected TraceSpanDataBuilder createChildSpanDataBuilder(
      TraceContext childContext, String childSpanName, BigInteger parentSpanID) {
    return new DefaultTraceSpanDataBuilder(childContext, childSpanName, parentSpanID);
  }
}
