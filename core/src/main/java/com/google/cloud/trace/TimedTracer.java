package com.google.cloud.trace;

import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.TraceContext;

public interface TimedTracer {
  TraceContext startSpan(TraceContext parentContext, SpanKind spanKind, String name);
  void endSpan(TraceContext context);
  void annotateSpan(TraceContext context, Labels labels);
  void setStackTrace(TraceContext context, StackTrace stackTrace);
}
