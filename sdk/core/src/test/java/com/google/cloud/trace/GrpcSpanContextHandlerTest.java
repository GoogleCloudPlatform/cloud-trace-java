package com.google.cloud.trace;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import java.math.BigInteger;
import org.junit.Test;

public class GrpcSpanContextHandlerTest {
  private static final SpanContext first = new SpanContext(new TraceId(BigInteger.valueOf(1)), new SpanId(1), new TraceOptions(1));
  private static final SpanContext second = new SpanContext(new TraceId(BigInteger.valueOf(2)), new SpanId(2), new TraceOptions(2));
  private static final SpanContext third = new SpanContext(new TraceId(BigInteger.valueOf(3)), new SpanId(3), new TraceOptions(3));

  @Test
  public void testAttachDetach() throws Exception {
    SpanContextHandler contextHandler = new GrpcSpanContextHandler(first);
    assertThat(contextHandler.current()).isEqualTo(first);
    assertThat(contextHandler.attach(second)).isEqualTo(first);
    assertThat(contextHandler.current()).isEqualTo(second);
    assertThat(contextHandler.attach(third)).isEqualTo(second);
    assertThat(contextHandler.current()).isEqualTo(third);
    contextHandler.detach(second);
    assertThat(contextHandler.current()).isEqualTo(second);
    contextHandler.detach(first);
    assertThat(contextHandler.current()).isEqualTo(first);
  }
}
