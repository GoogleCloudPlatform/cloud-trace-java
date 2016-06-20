package com.google.cloud.trace.v1;

import com.google.cloud.trace.RawTracer;
import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.Timestamp;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceContextFactory;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.cloud.trace.v1.source.TraceSource;
import com.google.devtools.cloudtrace.v1.Trace;

public class RawTracerV1 implements RawTracer {
  private final String projectId;
  private final TraceSource traceSource;
  private final TraceSink traceSink;

  public RawTracerV1(String projectId, TraceSource traceSource, TraceSink traceSink) {
    this.projectId = projectId;
    this.traceSource = traceSource;
    this.traceSink = traceSink;
  }

  @Override
  public void startSpan(TraceContext context, TraceContext parentContext,
      SpanKind spanKind, String name, Timestamp timestamp) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceSource.generateStartSpan(
          projectId, context, parentContext, spanKind, name, timestamp);
      traceSink.receive(trace);
    }
  }

  @Override
  public void endSpan(TraceContext context, Timestamp timestamp) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceSource.generateEndSpan(projectId, context, timestamp);
      traceSink.receive(trace);
    }
  }

  @Override
  public void annotateSpan(TraceContext context, Labels labels) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceSource.generateAnnotateSpan(projectId, context, labels);
      traceSink.receive(trace);
    }
  }

  @Override
  public void setStackTrace(TraceContext context, StackTrace stackTrace) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceSource.generateSetStackTrace(projectId, context, stackTrace);
      traceSink.receive(trace);
    }
  }
}
