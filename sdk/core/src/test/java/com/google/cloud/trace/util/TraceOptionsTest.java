package com.google.cloud.trace.util;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class TraceOptionsTest {
  private static final TraceOptions defaultTraceOptions = new TraceOptions();
  private static final TraceOptions noneEnabled = new TraceOptions(0);
  private static final TraceOptions traceEnabled = new TraceOptions(1);
  private static final TraceOptions stackTraceEnabled = new TraceOptions(2);
  private static final TraceOptions allEnabled = new TraceOptions(3);

  @Test
  public void testGetOptionsMask() {
    assertThat(defaultTraceOptions.getOptionsMask()).isEqualTo(0);
    assertThat(noneEnabled.getOptionsMask()).isEqualTo(0);
    assertThat(traceEnabled.getOptionsMask()).isEqualTo(1);
    assertThat(stackTraceEnabled.getOptionsMask()).isEqualTo(2);
    assertThat(allEnabled.getOptionsMask()).isEqualTo(3);
  }

  @Test
  public void testGetTraceEnabled() {
    assertThat(noneEnabled.getTraceEnabled()).isFalse();
    assertThat(traceEnabled.getTraceEnabled()).isTrue();
    assertThat(stackTraceEnabled.getTraceEnabled()).isFalse();
    assertThat(allEnabled.getTraceEnabled()).isTrue();
  }

  @Test
  public void testGetStackTraceEnabled() {
    assertThat(noneEnabled.getStackTraceEnabled()).isFalse();
    assertThat(traceEnabled.getStackTraceEnabled()).isFalse();
    assertThat(stackTraceEnabled.getStackTraceEnabled()).isTrue();
    assertThat(allEnabled.getStackTraceEnabled()).isTrue();
  }

  @Test
  public void testOverrideTraceEnabledFalse() {
    assertThat(noneEnabled.overrideTraceEnabled(false).getOptionsMask()).isEqualTo(0);
    assertThat(traceEnabled.overrideTraceEnabled(false).getOptionsMask()).isEqualTo(0);
    assertThat(stackTraceEnabled.overrideTraceEnabled(false).getOptionsMask()).isEqualTo(2);
    assertThat(allEnabled.overrideTraceEnabled(false).getOptionsMask()).isEqualTo(2);
  }

  @Test
  public void testOverrideTraceEnabledTrue() {
    assertThat(noneEnabled.overrideTraceEnabled(true).getOptionsMask()).isEqualTo(1);
    assertThat(traceEnabled.overrideTraceEnabled(true).getOptionsMask()).isEqualTo(1);
    assertThat(stackTraceEnabled.overrideTraceEnabled(true).getOptionsMask()).isEqualTo(3);
    assertThat(allEnabled.overrideTraceEnabled(true).getOptionsMask()).isEqualTo(3);
  }

  @Test
  public void testOverrideStackTraceEnabledFalse() {
    assertThat(noneEnabled.overrideStackTraceEnabled(false).getOptionsMask()).isEqualTo(0);
    assertThat(traceEnabled.overrideStackTraceEnabled(false).getOptionsMask()).isEqualTo(1);
    assertThat(stackTraceEnabled.overrideStackTraceEnabled(false).getOptionsMask()).isEqualTo(0);
    assertThat(allEnabled.overrideStackTraceEnabled(false).getOptionsMask()).isEqualTo(1);
  }

  @Test
  public void testOverrideStackTraceEnabledTrue() {
    assertThat(noneEnabled.overrideStackTraceEnabled(true).getOptionsMask()).isEqualTo(2);
    assertThat(traceEnabled.overrideStackTraceEnabled(true).getOptionsMask()).isEqualTo(3);
    assertThat(stackTraceEnabled.overrideStackTraceEnabled(true).getOptionsMask()).isEqualTo(2);
    assertThat(allEnabled.overrideStackTraceEnabled(true).getOptionsMask()).isEqualTo(3);
  }

  @Test
  public void testForTraceEnabled() {
    assertThat(TraceOptions.forTraceEnabled().getTraceEnabled()).isTrue();
  }

  @Test
  public void testForTraceDisabled() {
    assertThat(TraceOptions.forTraceDisabled().getTraceEnabled()).isFalse();
  }

  @Test
  public void testEquals() {
    assertThat(noneEnabled).isEqualTo(defaultTraceOptions);
    assertThat(noneEnabled).isEqualTo(noneEnabled);
    assertThat(stackTraceEnabled).isNotEqualTo(allEnabled);
  }

  @Test
  public void testHashCode() {
    assertThat(noneEnabled.hashCode()).isEqualTo(defaultTraceOptions.hashCode());
    assertThat(noneEnabled.hashCode()).isEqualTo(noneEnabled.hashCode());
    assertThat(stackTraceEnabled.hashCode()).isNotEqualTo(allEnabled.hashCode());
  }

  @Test
  public void testToString() {
    assertThat(traceEnabled.toString()).isEqualTo("TraceOptions{optionsMask=1}");
  }
}
