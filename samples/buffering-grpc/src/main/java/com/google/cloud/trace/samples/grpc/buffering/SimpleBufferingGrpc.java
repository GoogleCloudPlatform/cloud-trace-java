package com.google.cloud.trace.samples.grpc.buffering;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.trace.RawTracer;
import com.google.cloud.trace.TimestampFactoryTracer;
import com.google.cloud.trace.TraceContextFactoryTracer;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.grpc.v1.GrpcTraceSink;
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
import com.google.cloud.trace.v1.sink.FlushableTraceSink;
import com.google.cloud.trace.v1.sink.SimpleBufferingTraceSink;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.cloud.trace.v1.source.TraceSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleBufferingGrpc {
  public static void main(String[] args) throws IOException {
    String projectId = System.getProperty("projectId");
    String clientSecretsFile = System.getProperty("clientSecretsFile");

    // Create the raw tracer.
    TraceSource traceSource = new TraceSource();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    TraceSink traceSink = new GrpcTraceSink("cloudtrace.googleapis.com",
        GoogleCredentials.fromStream(new FileInputStream(clientSecretsFile))
            .createScoped(Arrays.asList("https://www.googleapis.com/auth/trace.append")),
        executor);
    FlushableTraceSink flushableSink = new SimpleBufferingTraceSink(traceSink);
    RawTracer rawTracer = new RawTracerV1(projectId, traceSource, traceSink);

    // Create the tracer.
    TraceContextFactory traceContextFactory = new TraceContextFactory(
        new ConstantTraceOptionsFactory(true, false));
    Tracer tracer = new TraceContextFactoryTracer(rawTracer, traceContextFactory);

    // Create the timestamp factory.
    TimestampFactory timestampFactory = new JavaTimestampFactory();

    // Create a tracer that supplies timestamps from the timestamp factory.
    TimestampFactoryTracer timestampTracer = new TimestampFactoryTracer(
        tracer, timestampFactory);

    // Create a span using the given timestamps.
    TraceContext context1 = timestampTracer.startSpan(
        traceContextFactory.rootContext(),
        SpanKind.RPC_CLIENT,
        "my span 1");

    TraceContext context2 = timestampTracer.startSpan(
        context1,
        SpanKind.RPC_CLIENT,
        "my span 2");

    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    timestampTracer.setStackTrace(context2, stackTraceBuilder.build());
    timestampTracer.endSpan(context2);

    timestampTracer.endSpan(context1);

    flushableSink.flush();

    executor.shutdownNow();
  }
}
