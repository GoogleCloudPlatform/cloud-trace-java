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

package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class TraceContext {
  private final TraceId traceId;
  private final SpanId spanId;
  private final TraceOptions traceOptions;

  public TraceContext(TraceId traceId, SpanId spanId, TraceOptions traceOptions) {
    this.traceId = traceId;
    this.spanId = spanId;
    this.traceOptions = traceOptions;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof TraceContext)) {
      return false;
    }

    TraceContext that = (TraceContext)obj;
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

  public TraceId getTraceId() {
    return traceId;
  }

  public SpanId getSpanId() {
    return spanId;
  }

  public TraceOptions getTraceOptions() {
    return traceOptions;
  }

  public TraceContext overrideOptions(TraceOptions overrideOptions) {
    return new TraceContext(traceId, spanId, overrideOptions);
  }
}
