package com.google.cloud.trace.util;


import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;
import org.junit.Test;

public class TraceContextFactoryTest {

  @Test
  public void testChildContextWithInvalidParent() {
    TestTraceOptionsFactory optionsFactory = new TestTraceOptionsFactory();
    TraceContextFactory contextFactory = new TraceContextFactory(optionsFactory,
        new SequentialTraceIdFactory(), new SequentialSpanIdFactory());
    TraceContext initialContext = contextFactory.initialContext();

    optionsFactory.toReturn = TraceOptions.forTraceEnabled();
    TraceContext child1 = contextFactory.childContext(initialContext);
    // The child context should have a valid Trace and Span Id.
    assertThat(child1.getTraceId().isValid()).isTrue();
    assertThat(child1.getSpanId().isValid()).isTrue();
    // A sampling decision should be made.
    assertThat(child1.getTraceOptions().getTraceEnabled()).isTrue();

    optionsFactory.toReturn = TraceOptions.forTraceDisabled();
    TraceContext child2 = contextFactory.childContext(initialContext);
    // The child context should have a valid Trace and Span Id.
    assertThat(child2.getTraceId().isValid()).isTrue();
    assertThat(child2.getSpanId().isValid()).isTrue();
    // A sampling decision should be made.
    assertThat(child2.getTraceOptions().getTraceEnabled()).isFalse();

    // Since child1 and child2 are both children of an invalid context, they should
    // have different TraceIds
    assertThat(child1.getTraceId().getTraceId()).isNotEqualTo(child2.getTraceId().getTraceId());
  }

  @Test
  public void testChildContextWithValidParent() {
    TestTraceOptionsFactory optionsFactory = new TestTraceOptionsFactory();
    TraceContextFactory contextFactory = new TraceContextFactory(optionsFactory,
        new SequentialTraceIdFactory(), new SequentialSpanIdFactory());
    TraceContext initialContext = contextFactory.initialContext();

    optionsFactory.toReturn = TraceOptions.forTraceEnabled();
    TraceContext child1 = contextFactory.childContext(initialContext);
    assertThat(child1.getTraceOptions().getTraceEnabled()).isTrue();

    optionsFactory.toReturn = TraceOptions.forTraceDisabled();
    TraceContext child2 = contextFactory.childContext(child1);

    assertThat(child1.getTraceId()).isEqualTo(child2.getTraceId());
    assertThat(child1.getSpanId()).isNotEqualTo(child2.getSpanId());
    assertThat(child1.getTraceOptions()).isEqualTo(child2.getTraceOptions());
  }

  private static class SequentialTraceIdFactory implements IdFactory<TraceId> {
    private long id = 1;

    @Override
    public TraceId nextId() {
      return new TraceId(BigInteger.valueOf(id++));
    }

    @Override
    public TraceId invalid() {
      return new TraceId(BigInteger.valueOf(0));
    }
  }

  private static class SequentialSpanIdFactory implements IdFactory<SpanId> {
    private long id = 1;

    @Override
    public SpanId nextId() {
      return new SpanId(id++);
    }

    @Override
    public SpanId invalid() {
      return new SpanId(0);
    }
  }

  private static class TestTraceOptionsFactory implements TraceOptionsFactory {
    TraceOptions toReturn = new TraceOptions();

    @Override
    public TraceOptions create() {
      return toReturn;
    }

    @Override
    public TraceOptions create(TraceOptions parent) {
      return parent;
    }
  }
}