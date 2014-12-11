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
 * Identifies the trace and span. Potentially forwarded over the wire for use
 * in propagation to child spans.
 */
public class TraceContext {
  private final String traceId;
  private final long spanId;
  
  /**
   * Creates a new trace context with the given identifiers.
   */
  public TraceContext(String traceId, long spanId) {
    this.traceId = traceId;
    this.spanId = spanId;
  }

  public String getTraceId() {
    return traceId;
  }

  public long getSpanId() {
    return spanId;
  }
}
