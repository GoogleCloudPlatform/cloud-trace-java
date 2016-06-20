package com.google.cloud.trace;

import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.Timestamp;
import com.google.cloud.trace.util.TimestampFactory;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceOptions;

import java.util.logging.Logger;

public class TraceContextHandlerTracer
    implements Tracer, TimedTracer, ManagedTracer  {
  private static final Logger logger = Logger.getLogger(TraceContextHandlerTracer.class.getName());

  private final Tracer tracer;
  private final TimestampFactory timestampFactory;
  private final TraceContextHandler contextHandler;

  public TraceContextHandlerTracer(Tracer tracer, TimestampFactory timestampFactory,
      TraceContextHandler contextHandler) {
    this.tracer = tracer;
    this.timestampFactory = timestampFactory;
    this.contextHandler = contextHandler;
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

  @Override
  public void startSpan(SpanKind spanKind, String name) {
    TraceContext context = tracer.startSpan(
        contextHandler.current(), spanKind, name, timestampFactory.now());
    contextHandler.push(context);
  }

  @Override
  public void startSpan(SpanKind spanKind, String name, TraceOptions traceOptions) {
    TraceContext context = tracer.startSpan(
        contextHandler.current().overrideOptions(traceOptions), spanKind, name,
        timestampFactory.now());
    contextHandler.push(context);
  }

  @Override
  public void endSpan() {
    TraceContext context = contextHandler.pop();
    if (context != null) {
      tracer.endSpan(context, timestampFactory.now());
    } else {
      logger.warning("Too many calls to ContextHandlerTraceClient.endCurrentSpan().");
    }
  }

  @Override
  public void annotateSpan(Labels labels) {
    tracer.annotateSpan(contextHandler.current(), labels);
  }

  @Override
  public void setStackTrace(StackTrace stackTrace) {
    tracer.setStackTrace(contextHandler.current(), stackTrace);
  }

  @Override
  public TraceContext getCurrentTraceContext() {
    return contextHandler.current();
  }
}
