package com.google.cloud.trace.guice;

import com.google.cloud.trace.RawTracer;
import com.google.cloud.trace.TraceContextFactoryTracer;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.util.TraceContextFactory;
import com.google.cloud.trace.util.TraceOptionsFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.Set;

public class TraceContextFactoryTracerModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  TraceContextFactory provideTraceContextFactory(TraceOptionsFactory traceOptionsFactory) {
    return new TraceContextFactory(traceOptionsFactory);
  }

  @Provides
  @Singleton
  Tracer provideTracer(Set<RawTracer> tracers, TraceContextFactory traceContextFactory) {
    return new TraceContextFactoryTracer(tracers, traceContextFactory);
  }
}
