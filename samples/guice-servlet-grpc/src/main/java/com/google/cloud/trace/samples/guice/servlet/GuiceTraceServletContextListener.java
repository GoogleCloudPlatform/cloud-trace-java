package com.google.cloud.trace.samples.guice.servlet;

import com.google.cloud.trace.guice.JavaTimestampFactoryModule;
import com.google.cloud.trace.guice.NaiveSamplingRateJndiModule;
import com.google.cloud.trace.guice.NaiveSamplingTraceOptionsFactoryModule;
import com.google.cloud.trace.guice.SingleThreadScheduledExecutorModule;
import com.google.cloud.trace.guice.StackTraceDisabledModule;
import com.google.cloud.trace.guice.TraceContextFactoryTracerModule;
import com.google.cloud.trace.guice.annotation.ManagedTracerSpanModule;
import com.google.cloud.trace.guice.api.ApiHostModule;
import com.google.cloud.trace.guice.auth.ClientSecretsFileJndiModule;
import com.google.cloud.trace.guice.auth.ClientSecretsGoogleCredentialsModule;
import com.google.cloud.trace.guice.auth.TraceAppendScopesModule;
import com.google.cloud.trace.guice.grpc.v1.GrpcTraceSinkModule;
import com.google.cloud.trace.guice.servlet.RequestDefaultTraceContextHandlerModule;
import com.google.cloud.trace.guice.servlet.RequestLabelerModule;
import com.google.cloud.trace.guice.servlet.RequestTraceContextHandlerTracerModule;
import com.google.cloud.trace.guice.servlet.UninitializedRequestTraceContextModule;
import com.google.cloud.trace.guice.v1.ProjectIdJndiModule;
import com.google.cloud.trace.guice.v1.RoughTraceSizerModule;
import com.google.cloud.trace.guice.v1.ScheduledBufferingTraceSinkModule;
import com.google.cloud.trace.guice.v1.SinkBufferSizeJndiModule;
import com.google.cloud.trace.guice.v1.SinkScheduledDelayJndiModule;
import com.google.cloud.trace.guice.v1.RawTracerV1Module;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceTraceServletContextListener extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(
        new RequestTraceContextHandlerTracerModule(),
        new TraceContextFactoryTracerModule(),
        new JavaTimestampFactoryModule(),
        new RequestDefaultTraceContextHandlerModule(),
        new RawTracerV1Module(),
        new NaiveSamplingTraceOptionsFactoryModule(),
        new ProjectIdJndiModule(),
        new ScheduledBufferingTraceSinkModule(),
        new NaiveSamplingRateJndiModule(),
        new StackTraceDisabledModule(),
        new GrpcTraceSinkModule(),
        new RoughTraceSizerModule(),
        new SinkBufferSizeJndiModule(),
        new SinkScheduledDelayJndiModule(),
        new SingleThreadScheduledExecutorModule(),
        new ApiHostModule(),
        new ClientSecretsGoogleCredentialsModule(),
        new ClientSecretsFileJndiModule(),
        new TraceAppendScopesModule(),
        new ManagedTracerSpanModule(),
        new RequestLabelerModule(),
        new GuiceServletModule(),
        new UninitializedRequestTraceContextModule());
  }
}
