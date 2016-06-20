package com.google.cloud.trace.guice.servlet;

import com.google.cloud.trace.DefaultTraceContextHandler;
import com.google.cloud.trace.TraceContextHandler;
import com.google.cloud.trace.util.TraceContext;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;

public class RequestDefaultTraceContextHandlerModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @RequestScoped
  TraceContextHandler provideTraceContextHandler(
      @RequestContext TraceContext traceContext) {
    return new DefaultTraceContextHandler(traceContext);
  }
}
