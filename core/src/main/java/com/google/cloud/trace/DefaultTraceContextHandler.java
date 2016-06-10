package com.google.cloud.trace;

import com.google.cloud.trace.util.TraceContext;

public class DefaultTraceContextHandler extends AbstractTraceContextHandler {
  public DefaultTraceContextHandler(TraceContext context) {
    super(context);
  }

  @Override
  public void doPush(TraceContext context) {
    // Do nothing else.
  }

  @Override
  public void doPop(TraceContext context) {
    // Do nothing else.
  }
}
