package com.google.cloud.trace.samples.logging.basic;

import com.google.cloud.trace.RawTracer;
import com.google.cloud.trace.TimestampFactoryTracer;
import com.google.cloud.trace.TraceContextFactoryTracer;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.util.ConstantTraceOptionsFactory;
import com.google.cloud.trace.util.JavaTimestampFactory;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.ThrowableStackTraceHelper;
import com.google.cloud.trace.util.TimestampFactory;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceContextFactory;
import com.google.cloud.trace.util.TraceOptions;
import com.google.cloud.trace.v1.RawTracerV1;
import com.google.cloud.trace.v1.sink.LoggingTraceSink;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.cloud.trace.v1.source.TraceSource;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicLogging {
  private final static Logger logger = Logger.getLogger(BasicLogging.class.getName());

  public static void main(String[] args) {
    // Create the raw tracer.
    TraceSource traceSource = new TraceSource();
    TraceSink traceSink = new LoggingTraceSink(logger, Level.WARNING);
    RawTracer rawTracer = new RawTracerV1("1", traceSource, traceSink);

    // Create the tracer.
    TraceContextFactory traceContextFactory = new TraceContextFactory(
        new ConstantTraceOptionsFactory(true, false));
    Tracer tracer = new TraceContextFactoryTracer(rawTracer, traceContextFactory);

    // Create the timestamp factory.
    TimestampFactory timestampFactory = new JavaTimestampFactory();

    // Create a span using the given timestamps.
    TraceContext context1 = tracer.startSpan(
        traceContextFactory.rootContext(),
        SpanKind.RPC_CLIENT,
        "my span 1",
        timestampFactory.now());
    tracer.endSpan(context1, timestampFactory.now());

    // Create a tacer that supplies timestamps from the timestamp factory.
    TimestampFactoryTracer timestampTracer = new TimestampFactoryTracer(
        tracer, timestampFactory);

    // Create a span whose timestamps come from the timestamp factory.
    TraceContext context2 = timestampTracer.startSpan(
        traceContextFactory.rootContext(),
        SpanKind.RPC_CLIENT,
        "my span 2");

    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    timestampTracer.setStackTrace(context2, stackTraceBuilder.build());
    timestampTracer.endSpan(context2);
  }
}
