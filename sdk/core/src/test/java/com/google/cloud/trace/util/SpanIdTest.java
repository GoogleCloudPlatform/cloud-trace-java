package com.google.cloud.trace.util;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class SpanIdTest {
  private static final SpanId first = new SpanId(10);
  private static final SpanId second = new SpanId(20);
  private static final SpanId invalid = new SpanId(0);
  private static final SpanId negative = new SpanId(-10);

  @Test
  public void testGetSpanId() {
    assertThat(first.getSpanId()).isEqualTo(10);
    assertThat(second.getSpanId()).isEqualTo(20);
  }

  @Test
  public void testIsValid() {
    assertThat(first.isValid()).isTrue();
    assertThat(invalid.isValid()).isFalse();
  }

  @Test
  public void testEquals() {
    assertThat(first).isEqualTo(first);
    assertThat(first).isNotEqualTo(second);
  }

  @Test
  public void testHashCode() {
    assertThat(first.hashCode()).isEqualTo(first.hashCode());
    assertThat(first.hashCode()).isNotEqualTo(second.hashCode());
  }

  @Test
  public void testToString() {
    assertThat(first.toString()).isEqualTo("SpanId{spanId=10}");
    assertThat(negative.toString()).isEqualTo("SpanId{spanId=18446744073709551606}");
  }
}
