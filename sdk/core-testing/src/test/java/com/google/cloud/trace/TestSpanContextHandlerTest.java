package com.google.cloud.trace;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import java.math.BigInteger;
import org.junit.Test;

public class TestSpanContextHandlerTest {
  private static final SpanContext context = new SpanContext(
      new TraceId(BigInteger.valueOf(7654)),
      new SpanId(3210),
      new TraceOptions(3));

  @Test
  public void testTestSpanContextHandler() {
    TestSpanContextHandler handler1 = new TestSpanContextHandler();
    assertThat(handler1.current()).isEqualTo(TestSpanContextHandler.TEST_CONTEXT);

    TestSpanContextHandler handler2 = new TestSpanContextHandler(
        TestSpanContextHandler.TEST_CONTEXT);
    assertThat(handler2.current()).isEqualTo(TestSpanContextHandler.TEST_CONTEXT);
  }

  @Test
  public void testCurrent() {
    TestSpanContextHandler handler = new TestSpanContextHandler(context);
    assertThat(handler.current()).isEqualTo(context);
  }

  @Test
  public void testAttach() {
    TestSpanContextHandler handler = new TestSpanContextHandler();
    SpanContext previous = handler.attach(context);
    assertThat(handler.current()).isEqualTo(context);
    assertThat(previous).isEqualTo(TestSpanContextHandler.TEST_CONTEXT);
  }

  @Test
  public void testDetach() {
    TestSpanContextHandler handler = new TestSpanContextHandler();
    handler.detach(context);
    assertThat(handler.current()).isEqualTo(context);
  }
}
