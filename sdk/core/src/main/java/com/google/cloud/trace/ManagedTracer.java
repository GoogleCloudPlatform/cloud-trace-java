package com.google.cloud.trace;

import com.google.cloud.trace.util.EndSpanOptions;
import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.StartSpanOptions;
import com.google.cloud.trace.util.TraceContext;

public interface ManagedTracer {
  void startSpan(String name);
  void startSpan(String name, StartSpanOptions options);
  void endSpan();
  void endSpan(EndSpanOptions options);
  void annotateSpan(Labels labels);
  void setStackTrace(StackTrace stackTrace);
  TraceContext getCurrentTraceContext();
}
