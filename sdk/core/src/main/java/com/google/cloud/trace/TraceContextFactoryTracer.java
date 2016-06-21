package com.google.cloud.trace;

import com.google.cloud.trace.util.EndSpanOptions;
import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.StartSpanOptions;
import com.google.cloud.trace.util.Timestamp;
import com.google.cloud.trace.util.TimestampFactory;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceContextFactory;
import com.google.cloud.trace.util.TraceOptions;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class TraceContextFactoryTracer implements Tracer {
  private final ImmutableSet<RawTracer> tracers;
  private final TraceContextFactory traceContextFactory;
  private final TimestampFactory timestampFactory;

  public TraceContextFactoryTracer(Set<RawTracer> tracers, TraceContextFactory traceContextFactory,
      TimestampFactory timestampFactory) {
    this.tracers = ImmutableSet.copyOf(tracers);
    this.traceContextFactory = traceContextFactory;
    this.timestampFactory = timestampFactory;
  }

  public TraceContextFactoryTracer(RawTracer tracer, TraceContextFactory traceContextFactory,
      TimestampFactory timestampFactory) {
    this(ImmutableSet.of(tracer), traceContextFactory, timestampFactory);
  }

  @Override
  public TraceContext startSpan(TraceContext parentContext, String name) {
    return startSpanOptions(parentContext, name, null, null, null);
  }

  @Override
  public TraceContext startSpan(TraceContext parentContext, String name, StartSpanOptions options) {
    return startSpanOptions(parentContext, name, options.getTimestamp(), options.getSpanKind(),
        options.getTraceOptions());
  }

  @Override
  public void endSpan(TraceContext context) {
    endSpanOptions(context, null);
  }

  @Override
  public void endSpan(TraceContext context, EndSpanOptions options) {
    endSpanOptions(context, options.getTimestamp());
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

  private TraceContext startSpanOptions(TraceContext parentContext, String name, Timestamp timestamp,
      SpanKind spanKind, TraceOptions traceOptions) {
    if (timestamp == null) {
      timestamp = timestampFactory.now();
    }
    if (spanKind == null) {
      spanKind = SpanKind.UNSPECIFIED;
    }
    if (traceOptions != null) {
      parentContext = parentContext.overrideOptions(traceOptions);
    }
    TraceContext context = traceContextFactory.childContext(parentContext);
    for (RawTracer tracer : tracers) {
      tracer.startSpan(context, parentContext, spanKind, name, timestamp);
    }
    return context;
  }

  private void endSpanOptions(TraceContext context, Timestamp timestamp) {
    if (timestamp == null) {
      timestamp = timestampFactory.now();
    }
    for (RawTracer tracer : tracers) {
      tracer.endSpan(context, timestamp);
    }
  }
}
