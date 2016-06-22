package com.google.cloud.trace.samples.logging.basic;

import com.google.cloud.trace.RawTracer;
import com.google.cloud.trace.TraceContextFactoryTracer;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.util.ConstantTraceOptionsFactory;
import com.google.cloud.trace.util.JavaTimestampFactory;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.ThrowableStackTraceHelper;
import com.google.cloud.trace.util.TimestampFactory;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceContextFactory;
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
    TimestampFactory timestampFactory = new JavaTimestampFactory();
    Tracer tracer = new TraceContextFactoryTracer(rawTracer, traceContextFactory, timestampFactory);

    // Create a span using the given timestamps.
    TraceContext context1 = tracer.startSpan(traceContextFactory.rootContext(), "my span 1");
    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    tracer.setStackTrace(context1, stackTraceBuilder.build());
    tracer.endSpan(context1);
  }
}
