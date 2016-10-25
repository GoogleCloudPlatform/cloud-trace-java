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

import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.TraceSpan;
import com.google.devtools.cloudtrace.v1.Traces;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that buffers trace messages. Trace messages added to this buffer are combined with
 * existing trace messages when they contain the same trace identifiers, by combining the spans of
 * the matching trace messages.
 *
 * @see Traces
 * @see Trace
 */
public class TraceBuffer {
  private final HashMap<TraceKey, SpanBuffer> traceMap;

  /**
   * Creates a trace buffer.
   */
  public TraceBuffer() {
    this.traceMap = new HashMap<TraceKey, SpanBuffer>();
  }

  /**
   * Adds a trace message to this trace buffer.
   *
   * @param trace a trace message to add this trace buffer.
   */
  public void put(Trace trace) {
    TraceKey traceKey = new TraceKey(trace.getProjectId(), trace.getTraceId());
    SpanBuffer spans = traceMap.get(traceKey);
    if (spans == null) {
      spans = new SpanBuffer();
      traceMap.put(traceKey, spans);
    }
    for (TraceSpan span : trace.getSpansList()) {
      spans.put(span);
    }
  }

  /**
   * Returns all of the trace messages in this trace buffer.
   *
   * @return an iterable containing the trace messages in this trace buffer.
   */
  public Traces getTraces() {
    Traces.Builder tracesBuilder = Traces.newBuilder();
    for (Map.Entry<TraceKey, SpanBuffer> entry : traceMap.entrySet()) {
      Trace.Builder traceBuilder = Trace.newBuilder()
          .setProjectId(entry.getKey().getProjectId())
          .setTraceId(entry.getKey().getTraceId());
      for (TraceSpan.Builder spanBuilder : entry.getValue().getSpans()) {
        traceBuilder.addSpans(spanBuilder);
      }
      tracesBuilder.addTraces(traceBuilder);
    }
    return tracesBuilder.build();
  }

  /**
   * Returns true if the TraceBuffer is empty.
   */
  public boolean isEmpty() {
    return traceMap.isEmpty();
  }
}
