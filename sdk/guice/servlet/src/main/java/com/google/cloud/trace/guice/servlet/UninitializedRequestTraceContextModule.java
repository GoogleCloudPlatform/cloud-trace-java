package com.google.cloud.trace.guice.servlet;

import com.google.cloud.trace.util.TraceContext;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;

public class UninitializedRequestTraceContextModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @RequestContext
  @RequestScoped
  TraceContext provideUninitializedTraceContext() {
    throw new IllegalStateException("RequestContext must be initialized in the request scope.");
  }
}
