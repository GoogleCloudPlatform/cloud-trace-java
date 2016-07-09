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

package com.google.cloud.trace.v1.util;

import com.google.devtools.cloudtrace.v1.TraceSpan;
import java.util.HashMap;

/**
 * A buffer for trace span messages that combines messages for the same span.
 *
 * @see Iterable
 * @see TraceSpan
 * @see TraceSpan.Builder
 */
public class SpanBuffer {
  private final HashMap<Long, TraceSpan.Builder> spanMap;

  /**
   * Creates a span buffer.
   */
  public SpanBuffer() {
    this.spanMap = new HashMap<Long, TraceSpan.Builder>();
  }

  /**
   * Adds a trace span message to this span buffer.
   *
   * @param span the trace span message to add to this buffer.
   */
  public void put(TraceSpan span) {
    long spanId = span.getSpanId();
    TraceSpan.Builder builder = spanMap.get(spanId);
    if (builder == null) {
      spanMap.put(spanId, span.toBuilder());
    } else {
      spanMap.put(spanId, builder.mergeFrom(span));
    }
  }

  /**
   * Gets builders for all of the trace span messages contained in this span buffer.
   *
   * @return an iterable containing builders for all of the trace span messages contained in this
   * span buffer.
   */
  public Iterable<TraceSpan.Builder> getSpans() {
    return spanMap.values();
  }
}
