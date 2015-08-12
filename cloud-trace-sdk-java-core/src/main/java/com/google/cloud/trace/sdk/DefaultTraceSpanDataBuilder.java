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

/**
 * Exposes the data for creating new {@link TraceSpanData} instances.
 * TODO: Should this be ThreadTraceContext-aware? Or should there be another impl
 * that is?
 */
public class DefaultTraceSpanDataBuilder extends AbstractTraceSpanDataBuilder {
  private TraceContext context;
  private String name;
  private BigInteger parentSpanId;

  /**
   * Creates a new default trace span data builder with the given context and parent span id.
   */
  public DefaultTraceSpanDataBuilder(TraceContext context, String name, BigInteger parentSpanId) {
    this.context = context;
    this.name = name;
    this.parentSpanId = parentSpanId;
  }

  /**
   * Creates a new default trace span data builder that builds a root span on the given trace
   * with the given name.
   * TODO: Better integrate this with trace-enabling. Currently they will always write.
   */
  public DefaultTraceSpanDataBuilder(String traceId, String spanName) {
    this(new TraceContext(traceId, spanIdGenerator.generate(), TraceContext.TRACE_ENABLED),
        spanName, BigInteger.ZERO);
  }

  /**
   * Creates a new default trace span data builder for root spans on a new trace.
   */
  public DefaultTraceSpanDataBuilder(String spanName) {
    this(traceIdGenerator.generate(), spanName);
  }

  @Override
  public TraceContext getTraceContext() {
    return context;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public BigInteger getParentSpanId() {
    return parentSpanId;
  }
}
