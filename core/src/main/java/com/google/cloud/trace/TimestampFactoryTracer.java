package com.google.cloud.trace;

import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.Timestamp;
import com.google.cloud.trace.util.TimestampFactory;
import com.google.cloud.trace.util.TraceContext;

public class TimestampFactoryTracer implements Tracer, TimedTracer {
  private final Tracer tracer;
  private final TimestampFactory timestampFactory;

  public TimestampFactoryTracer(Tracer tracer, TimestampFactory timestampFactory) {
    this.tracer = tracer;
    this.timestampFactory = timestampFactory;
  }

  @Override
  public TraceContext startSpan(
      TraceContext parentContext, SpanKind spanKind, String name, Timestamp timestamp) {
    return tracer.startSpan(parentContext, spanKind, name, timestamp);
  }

  @Override
  public void endSpan(TraceContext context, Timestamp timestamp) {
    tracer.endSpan(context, timestamp);
  }

  @Override
  public void annotateSpan(TraceContext context, Labels labels) {
    tracer.annotateSpan(context, labels);
  }

  @Override
  public void setStackTrace(TraceContext context, StackTrace stackTrace) {
    tracer.setStackTrace(context, stackTrace);
  }

  @Override
  public TraceContext startSpan(TraceContext parentContext, SpanKind spanKind, String name) {
    return tracer.startSpan(parentContext, spanKind, name, timestampFactory.now());
  }

  @Override
  public void endSpan(TraceContext context) {
    tracer.endSpan(context, timestampFactory.now());
  }
}
