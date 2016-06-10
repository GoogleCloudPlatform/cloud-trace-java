package com.google.cloud.trace.v1.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.TraceSpan;

import java.util.HashMap;
import java.util.Map;

public class TraceBuffer {
  private final HashMap<TraceKey, SpanBuffer> traceMap;

  public TraceBuffer() {
    this.traceMap = new HashMap<TraceKey, SpanBuffer>();
  }

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

  public Iterable<Trace> getTraces() {
    return Iterables.transform(traceMap.entrySet(),
        new Function<Map.Entry<TraceKey, SpanBuffer>, Trace>(){
      @Override
      public Trace apply(Map.Entry<TraceKey, SpanBuffer> entry) {
        Trace.Builder traceBuilder = Trace.newBuilder()
            .setProjectId(entry.getKey().getProjectId())
            .setTraceId(entry.getKey().getTraceId());
        for (TraceSpan.Builder spanBuilder : entry.getValue().getSpans()) {
          traceBuilder.addSpans(spanBuilder);
        }
        return traceBuilder.build();
      }
    });
  }

  public void clear() {
    traceMap.clear();
  }
}
