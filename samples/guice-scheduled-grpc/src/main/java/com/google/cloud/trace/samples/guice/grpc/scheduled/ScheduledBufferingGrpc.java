package com.google.cloud.trace.samples.guice.grpc.scheduled;

import com.google.cloud.trace.ManagedTracer;
import com.google.cloud.trace.guice.ConstantTraceOptionsFactoryModule;
import com.google.cloud.trace.guice.DefaultTraceContextHandlerModule;
import com.google.cloud.trace.guice.JavaTimestampFactoryModule;
import com.google.cloud.trace.guice.SingleThreadScheduledExecutorModule;
import com.google.cloud.trace.guice.StackTraceDisabledModule;
import com.google.cloud.trace.guice.TraceContextFactoryTracerModule;
import com.google.cloud.trace.guice.TraceContextHandlerTracerModule;
import com.google.cloud.trace.guice.TraceEnabledModule;
import com.google.cloud.trace.guice.api.ApiHostModule;
import com.google.cloud.trace.guice.auth.ClientSecretsFilePropertiesModule;
import com.google.cloud.trace.guice.auth.ClientSecretsGoogleCredentialsModule;
import com.google.cloud.trace.guice.auth.TraceAppendScopesModule;
import com.google.cloud.trace.guice.grpc.v1.GrpcTraceSinkModule;
import com.google.cloud.trace.guice.v1.ProjectIdPropertiesModule;
import com.google.cloud.trace.guice.v1.RoughTraceSizerModule;
import com.google.cloud.trace.guice.v1.ScheduledBufferingTraceSinkModule;
import com.google.cloud.trace.guice.v1.SinkBufferSizePropertiesModule;
import com.google.cloud.trace.guice.v1.SinkScheduledDelayPropertiesModule;
import com.google.cloud.trace.guice.v1.RawTracerV1Module;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.ThrowableStackTraceHelper;
import com.google.cloud.trace.util.TraceOptions;
import com.google.cloud.trace.v1.sink.FlushableTraceSink;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ScheduledBufferingGrpc {
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(
        new TraceContextHandlerTracerModule(),
        new TraceContextFactoryTracerModule(),
        new JavaTimestampFactoryModule(),
        new DefaultTraceContextHandlerModule(),
        new RawTracerV1Module(),
        new ConstantTraceOptionsFactoryModule(),
        new ProjectIdPropertiesModule(),
        new ScheduledBufferingTraceSinkModule(),
        new TraceEnabledModule(),
        new StackTraceDisabledModule(),
        new GrpcTraceSinkModule(),
        new RoughTraceSizerModule(),
        new SinkBufferSizePropertiesModule(),
        new SinkScheduledDelayPropertiesModule(),
        new SingleThreadScheduledExecutorModule(),
        new ApiHostModule(),
        new ClientSecretsGoogleCredentialsModule(),
        new ClientSecretsFilePropertiesModule(),
        new TraceAppendScopesModule());

    ManagedTracer tracer = injector.getInstance(ManagedTracer.class);

    tracer.startSpan(SpanKind.RPC_CLIENT, "my span 1", TraceOptions.forTraceEnabled());
    tracer.startSpan(SpanKind.RPC_CLIENT, "my span 2");

    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    tracer.setStackTrace(stackTraceBuilder.build());
    tracer.endSpan();

    tracer.endSpan();

    FlushableTraceSink flushableSink = injector.getInstance(FlushableTraceSink.class);

    flushableSink.flush();
  }
}
