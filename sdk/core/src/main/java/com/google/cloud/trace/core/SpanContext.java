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

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * A class that represents a span context. A span context is the identifiers associated with a
 * span and a set of options that determine how a span is traced.
 *
 * @see SpanId
 * @see SpanContextFactory
 * @see TraceOptions
 * @see TraceId
 */
public class SpanContext {
  private final TraceId traceId;
  private final SpanId spanId;
  private final TraceOptions traceOptions;

  /**
   * Creates a span context.
   *
   * @param traceId      the trace identifier of the span context.
   * @param spanId       the span identifier of the span context.
   * @param traceOptions the trace options for the span context.
   */
  public SpanContext(TraceId traceId, SpanId spanId, TraceOptions traceOptions) {
    this.traceId = traceId;
    this.spanId = spanId;
    this.traceOptions = traceOptions;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof SpanContext)) {
      return false;
    }

    SpanContext that = (SpanContext)obj;
    return Objects.equals(traceId, that.traceId)
        && Objects.equals(spanId, that.spanId)
        && Objects.equals(traceOptions, that.traceOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(traceId, spanId, traceOptions);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("traceId", traceId)
        .add("spanId", spanId)
        .add("traceOptions", traceOptions)
        .toString();
  }

  /**
   * Returns the trace identifier.
   *
   * @return the trace identifier.
   */
  public TraceId getTraceId() {
    return traceId;
  }

  /**
   * Returns the span identifier.
   *
   * @return the span identifier.
   */
  public SpanId getSpanId() {
    return spanId;
  }

  /**
   * Returns the trace options.
   *
   * @return the trace options.
   */
  public TraceOptions getTraceOptions() {
    return traceOptions;
  }

  /**
   * Generates a new span context based on this span context and the given trace options.
   *
   * @param overrideOptions a trace options set on the new span context.
   * @return the new span context.
   */
  public SpanContext overrideOptions(TraceOptions overrideOptions) {
    return new SpanContext(traceId, spanId, overrideOptions);
  }
}
