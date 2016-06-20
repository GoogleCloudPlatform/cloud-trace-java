package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;

import java.time.Instant;

public class JavaTimestamp implements Timestamp {
  private final Instant instant;

  public JavaTimestamp(Instant instant) {
    this.instant = instant;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("instant", instant)
        .toString();
  }

  @Override
  public long getSeconds() {
    return instant.getEpochSecond();
  }

  @Override
  public int getNanos() {
    return instant.getNano();
  }
}
