package com.google.cloud.trace;

import com.google.cloud.trace.util.TraceContext;

public interface TraceContextHandler {
  TraceContext current();
  void push(TraceContext context);
  TraceContext pop();
}
