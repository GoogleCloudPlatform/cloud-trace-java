package com.google.cloud.trace.guice.servlet;

import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceContextFactory;
import com.google.cloud.trace.util.TraceOptions;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Singleton;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Singleton
public class RequestTraceContextFilter implements Filter {
  private final TraceContextFactory traceContextFactory;

  @Inject
  RequestTraceContextFilter(TraceContextFactory traceContextFactory) {
    this.traceContextFactory = traceContextFactory;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}

  @Override
  public void doFilter(
      ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;

    String contextHeader = httpRequest.getHeader(TraceContextFactory.headerKey());
    TraceContext context;
    if (contextHeader != null) {
      context = traceContextFactory.fromHeader(contextHeader);
    } else {
      context = traceContextFactory.rootContext();
    }

    httpRequest.setAttribute(
        Key.get(TraceContext.class, RequestContext.class).toString(), context);

    chain.doFilter(request, response);
  }
}
