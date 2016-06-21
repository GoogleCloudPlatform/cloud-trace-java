package com.google.cloud.trace.guice;

import com.google.cloud.trace.ManagedTracer;
import com.google.cloud.trace.TraceContextHandler;
import com.google.cloud.trace.TraceContextHandlerTracer;
import com.google.cloud.trace.Tracer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class TraceContextHandlerTracerModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  ManagedTracer provideManagedTracer(Tracer tracer, TraceContextHandler traceContextHandler) {
    return new TraceContextHandlerTracer(tracer, traceContextHandler);
  }
}
