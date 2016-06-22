package com.google.cloud.trace.v1.util;

import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.TraceSpan;

import java.util.Map;

public class RoughTraceSizer implements Sizer<Trace> {
  @Override
  public int size(Trace trace) {
    int size = 0;
    size += trace.getProjectId().length();
    size += trace.getTraceId().length();
    for (TraceSpan span : trace.getSpansList()) {
      size += spanSize(span);
    }
    return size;
  }

  private int spanSize(TraceSpan span) {
    int size = 0;
    size += 8; // For span_id.
    size += 4; // For kind.
    size += span.getName().length();
    size += 16; // For start_time.
    size += 16; // For end_time.
    size += 8; // For parent_span_id.
    size += labelsSize(span);
    return size;
  }

  private int labelsSize(TraceSpan span) {
    int size = 0;
    for (Map.Entry<String, String> label : span.getLabels().entrySet()) {
      size += label.getKey().length() + label.getValue().length();
    }
    return size;
  }
}
