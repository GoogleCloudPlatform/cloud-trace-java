package com.google.cloud.trace.core;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;
import org.junit.Test;

public class TraceContextFactoryTest {
  @Test
  public void testHeaderKey() {
    assertThat(TraceContextFactory.headerKey()).isEqualTo("X-Cloud-Trace-Context");
  }

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

  @Test
  public void testInitialContext() {
    TraceContextFactory factory = new TraceContextFactory(new TestTraceOptionsFactory(),
        new SequentialTraceIdFactory(), new SequentialSpanIdFactory());
    TraceContext initial = factory.initialContext();

    assertThat(initial.getTraceId().isValid()).isFalse();
    assertThat(initial.getSpanId().isValid()).isFalse();
    assertThat(initial.getTraceOptions().getOptionsMask()).isEqualTo(0);
  }

  @Test
  public void testFromHeader() {
    TestTraceOptionsFactory optionsFactory = new TestTraceOptionsFactory();
    TraceContextFactory factory = new TraceContextFactory(optionsFactory,
        new SequentialTraceIdFactory(), new SequentialSpanIdFactory());

    optionsFactory.toReturn = TraceOptions.forTraceEnabled();

    TraceContext empty = factory.fromHeader("");
    assertThat(empty.getTraceId().isValid()).isFalse();
    assertThat(empty.getSpanId().isValid()).isFalse();
    assertThat(empty.getTraceOptions().getOptionsMask()).isEqualTo(1);

    TraceContext badTraceId = factory.fromHeader("xyz");
    assertThat(badTraceId.getTraceId().isValid()).isFalse();
    assertThat(badTraceId.getSpanId().isValid()).isFalse();
    assertThat(badTraceId.getTraceOptions().getOptionsMask()).isEqualTo(1);

    TraceContext invalidTraceId = factory.fromHeader("fffffffffffffffffffffffffffffffff");
    assertThat(invalidTraceId.getTraceId().isValid()).isFalse();
    assertThat(invalidTraceId.getSpanId().isValid()).isFalse();
    assertThat(invalidTraceId.getTraceOptions().getOptionsMask()).isEqualTo(1);

    TraceContext badSpanId = factory.fromHeader("123456789abcdef0123456789abcdef0/m");
    assertThat(badSpanId.getTraceId())
        .isEqualTo(new TraceId(new BigInteger("123456789abcdef0123456789abcdef0", 16)));
    assertThat(badSpanId.getSpanId().isValid()).isFalse();
    assertThat(badSpanId.getTraceOptions().getOptionsMask()).isEqualTo(1);

    TraceContext invalidSpanId = factory.fromHeader("123456789abcdef0123456789abcdef0/0");
    assertThat(invalidSpanId.getTraceId())
        .isEqualTo(new TraceId(new BigInteger("123456789abcdef0123456789abcdef0", 16)));
    assertThat(invalidSpanId.getSpanId().isValid()).isFalse();
    assertThat(invalidSpanId.getTraceOptions().getOptionsMask()).isEqualTo(1);

    TraceContext badOptions = factory.fromHeader("123456789abcdef0123456789abcdef0/13;o=q");
    assertThat(badOptions.getTraceId())
        .isEqualTo(new TraceId(new BigInteger("123456789abcdef0123456789abcdef0", 16)));
    assertThat(badOptions.getSpanId()).isEqualTo(new SpanId(13));
    assertThat(badOptions.getTraceOptions().getOptionsMask()).isEqualTo(1);

    TraceContext context = factory.fromHeader("123456789abcdef0123456789abcdef0/13;o=3");
    assertThat(context.getTraceId())
        .isEqualTo(new TraceId(new BigInteger("123456789abcdef0123456789abcdef0", 16)));
    assertThat(context.getSpanId()).isEqualTo(new SpanId(13));
    assertThat(context.getTraceOptions().getOptionsMask()).isEqualTo(3);

    TraceContext contextPre = factory.fromHeader("123456789abcdef0123456789abcdef0/13;x=u;o=3");
    assertThat(contextPre.getTraceId())
        .isEqualTo(new TraceId(new BigInteger("123456789abcdef0123456789abcdef0", 16)));
    assertThat(contextPre.getSpanId()).isEqualTo(new SpanId(13));
    assertThat(contextPre.getTraceOptions().getOptionsMask()).isEqualTo(3);

    TraceContext contextPost = factory.fromHeader("123456789abcdef0123456789abcdef0/13;o=3;x=u");
    assertThat(contextPost.getTraceId())
        .isEqualTo(new TraceId(new BigInteger("123456789abcdef0123456789abcdef0", 16)));
    assertThat(contextPost.getSpanId()).isEqualTo(new SpanId(13));
    assertThat(contextPost.getTraceOptions().getOptionsMask()).isEqualTo(3);
  }

  @Test
  public void testToHeader() {
    TraceContext context = new TraceContext(
        new TraceId(BigInteger.valueOf(10)), new SpanId(20), new TraceOptions(3));
    assertThat(TraceContextFactory.toHeader(context))
        .isEqualTo("0000000000000000000000000000000a/20;o=3");
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
