package com.google.cloud.trace;

import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceOptions;

public interface ManagedTracer {
  void startSpan(SpanKind spanKind, String name);
  void startSpan(SpanKind spanKind, String name, TraceOptions traceOptions);
  void endSpan();
  void annotateSpan(Labels labels);
  void setStackTrace(StackTrace stackTrace);
  TraceContext getCurrentTraceContext();
}
