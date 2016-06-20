package com.google.cloud.trace;

import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.Timestamp;
import com.google.cloud.trace.util.TraceContext;

public class DoNothingRawTracer implements RawTracer {
  @Override
  public void startSpan(TraceContext context, TraceContext parentContext, SpanKind spanKind,
      String name, Timestamp timestamp) {
  }

  @Override
  public void endSpan(TraceContext context, Timestamp timestamp) {}

  @Override
  public void annotateSpan(TraceContext context, Labels labels) {}

  @Override
  public void setStackTrace(TraceContext context, StackTrace stackTrace) {}
}
