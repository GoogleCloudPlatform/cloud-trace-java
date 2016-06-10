package com.google.cloud.trace.v1.util;

import com.google.devtools.cloudtrace.v1.TraceSpan;

import java.util.HashMap;

public class SpanBuffer {
  private final HashMap<Long, TraceSpan.Builder> spanMap;

  public SpanBuffer() {
    this.spanMap = new HashMap<Long, TraceSpan.Builder>();
  }

  public void put(TraceSpan span) {
    long spanId = span.getSpanId();
    TraceSpan.Builder builder = spanMap.get(spanId);
    if (builder == null) {
      spanMap.put(spanId, span.toBuilder());
    } else {
      spanMap.put(spanId, builder.mergeFrom(span));
    }
  }

  public Iterable<TraceSpan.Builder> getSpans() {
    return spanMap.values();
  }
}
