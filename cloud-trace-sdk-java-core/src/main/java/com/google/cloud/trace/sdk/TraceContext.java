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

import java.math.BigInteger;

/**
 * Identifies the trace and span and whether it is currently slated to be written out.
 * Potentially forwarded over the wire for use in propagation to child spans.
 */
public class TraceContext {
  public static final long TRACE_ENABLED = 1;

  private final String traceId;
  private final BigInteger spanId;

  /**
   * The trace span options, which is a bitmasked long representing the state
   * of various tracing features (such as whether or not incoming/outgoing traces
   * are enabled).
   */
  private long options;

  /**
   * Creates a new trace context with the given identifiers and options.
   */
  public TraceContext(String traceId, BigInteger spanId, long options) {
    this.traceId = traceId;
    this.spanId = spanId;
    this.options = options;
  }

  public String getTraceId() {
    return traceId;
  }

  public BigInteger getSpanId() {
    return spanId;
  }

  public boolean getShouldWrite() {
    return (options & TRACE_ENABLED) == TRACE_ENABLED;
  }

  public void setShouldWrite(boolean shouldWrite) {
    if (shouldWrite) {
      this.options |= TRACE_ENABLED;
    } else {
      this.options &= ~TRACE_ENABLED;
    }
  }

  public long getOptions() {
    return options;
  }

  public void setOptions(long options) {
    this.options = options;
  }

  @Override
  public String toString() {
    return traceId + '|' + spanId + '|' + options;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (options ^ (options >>> 32));
    result = prime * result + ((spanId == null) ? 0 : spanId.hashCode());
    result = prime * result + ((traceId == null) ? 0 : traceId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TraceContext other = (TraceContext) obj;
    if (options != other.options)
      return false;
    if (spanId == null) {
      if (other.spanId != null)
        return false;
    } else if (!spanId.equals(other.spanId))
      return false;
    if (traceId == null) {
      if (other.traceId != null)
        return false;
    } else if (!traceId.equals(other.traceId))
      return false;
    return true;
  }
}
