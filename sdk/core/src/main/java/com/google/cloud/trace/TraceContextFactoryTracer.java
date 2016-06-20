package com.google.cloud.trace;

import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.Timestamp;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceContextFactory;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class TraceContextFactoryTracer implements Tracer {
  private final ImmutableSet<RawTracer> tracers;
  private final TraceContextFactory traceContextFactory;

  public TraceContextFactoryTracer(
      Set<RawTracer> tracers, TraceContextFactory traceContextFactory) {
    this.tracers = ImmutableSet.copyOf(tracers);
    this.traceContextFactory = traceContextFactory;
  }

  public TraceContextFactoryTracer(RawTracer tracer, TraceContextFactory traceContextFactory) {
    this(ImmutableSet.of(tracer), traceContextFactory);
  }

  @Override
  public TraceContext startSpan(
      TraceContext parentContext, SpanKind spanKind, String name, Timestamp timestamp) {
    TraceContext context = traceContextFactory.childContext(parentContext);
    for (RawTracer tracer : tracers) {
      tracer.startSpan(context, parentContext, spanKind, name, timestamp);
    }
    return context;
  }

  @Override
  public void endSpan(TraceContext context, Timestamp timestamp) {
    for (RawTracer tracer : tracers) {
      tracer.endSpan(context, timestamp);
    }
  }

  @Override
  public void annotateSpan(TraceContext context, Labels labels) {
    for (RawTracer tracer : tracers) {
      tracer.annotateSpan(context, labels);
    }
  }

  @Override
  public void setStackTrace(TraceContext context, StackTrace stackTrace) {
    for (RawTracer tracer : tracers) {
      tracer.setStackTrace(context, stackTrace);
    }
  }
}
