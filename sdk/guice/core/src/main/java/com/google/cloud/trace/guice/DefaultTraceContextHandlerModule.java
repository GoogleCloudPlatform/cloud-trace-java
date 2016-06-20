package com.google.cloud.trace.guice;

import com.google.cloud.trace.DefaultTraceContextHandler;
import com.google.cloud.trace.TraceContextHandler;
import com.google.cloud.trace.util.TraceContextFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class DefaultTraceContextHandlerModule extends AbstractModule {
  @Override
  protected void configure() {
  }

  @Provides
  @Singleton
  TraceContextHandler provideTraceContextHandler(TraceContextFactory traceContextFactory) {
    return new DefaultTraceContextHandler(traceContextFactory.initialContext());
  }
}
