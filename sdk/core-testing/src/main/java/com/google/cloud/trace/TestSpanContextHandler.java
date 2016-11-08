package com.google.cloud.trace;

import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import java.math.BigInteger;

public class TestSpanContextHandler implements SpanContextHandler {
  public static final SpanContext TEST_CONTEXT = new SpanContext(
      new TraceId(BigInteger.TEN),
      new SpanId(500),
      TraceOptions.forTraceEnabled());

  private SpanContext currentContext;

  public TestSpanContextHandler() {
    this(TEST_CONTEXT);
  }

  public TestSpanContextHandler(SpanContext context) {
    this.currentContext = context;
  }

  @Override
  public SpanContext current() {
    return currentContext;
  }

  @Override
  public SpanContext attach(SpanContext context) {
    SpanContext temp = currentContext;
    this.currentContext = context;
    return temp;
  }

  @Override
  public void detach(SpanContext toAttach) {
    this.currentContext = toAttach;
  }
}
