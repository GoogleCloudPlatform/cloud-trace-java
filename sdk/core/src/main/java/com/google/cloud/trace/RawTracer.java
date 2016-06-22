package com.google.cloud.trace;

import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.Timestamp;
import com.google.cloud.trace.util.TraceContext;

public interface RawTracer {
  void startSpan(TraceContext context, TraceContext parentContext, SpanKind spanKind,
      String name, Timestamp timestamp);
  void endSpan(TraceContext context, Timestamp timestamp);
  void annotateSpan(TraceContext context, Labels labels);
  void setStackTrace(TraceContext context, StackTrace stackTrace);
}
