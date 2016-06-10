package com.google.cloud.trace.samples.guice.servlet;

import com.google.cloud.trace.guice.servlet.RequestTraceContextFilter;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class GuiceServletModule extends ServletModule {
  @Override
  protected void configureServlets() {
    bind(GuiceServlet.class).in(Singleton.class);
    filter("/*").through(RequestTraceContextFilter.class);
    serve("/*").with(GuiceServlet.class);
  }
}
