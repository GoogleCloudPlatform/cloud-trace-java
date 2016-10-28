package com.google.cloud.trace.core;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;
import org.junit.Test;

public class TraceIdTest {
  private static final TraceId first = new TraceId(BigInteger.valueOf(10));
  private static final TraceId second = new TraceId(BigInteger.valueOf(20));
  private static final TraceId zero = new TraceId(BigInteger.valueOf(0));
  private static final TraceId negative = new TraceId(BigInteger.valueOf(-1));
  private static final TraceId tooLarge = new TraceId(
      new BigInteger("fffffffffffffffffffffffffffffffff", 16));

  @Test
  public void testGetTraceId() {
    assertThat(first.getTraceId()).isEqualTo(BigInteger.valueOf(10));
    assertThat(second.getTraceId()).isEqualTo(BigInteger.valueOf(20));
  }

  @Test
  public void testIsValid() {
    assertThat(first.isValid()).isTrue();
    assertThat(zero.isValid()).isFalse();
    assertThat(negative.isValid()).isFalse();
    assertThat(tooLarge.isValid()).isFalse();
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
    assertThat(first.toString()).isEqualTo("TraceId{traceId=0000000000000000000000000000000a}");
  }

  @Test
  public void testGetApiString() {
    assertThat(first.getApiString()).isEqualTo("0000000000000000000000000000000a");
  }
}
