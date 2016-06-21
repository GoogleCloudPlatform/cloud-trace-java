package com.google.cloud.trace;

import com.google.cloud.trace.util.EndSpanOptions;
import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.StartSpanOptions;
import com.google.cloud.trace.util.TraceContext;

public interface Tracer {
  TraceContext startSpan(TraceContext parentContext, String name);
  TraceContext startSpan(TraceContext parentContext, String name, StartSpanOptions options);
  void endSpan(TraceContext context);
  void endSpan(TraceContext context, EndSpanOptions options);
  void annotateSpan(TraceContext context, Labels labels);
  void setStackTrace(TraceContext context, StackTrace stackTrace);
}
