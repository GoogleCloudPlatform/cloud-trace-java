package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;

import org.joda.time.Instant;

public class JodaTimestamp implements Timestamp {
  private final Instant instant;

  public JodaTimestamp(Instant instant) {
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
    return instant.getMillis() / 1000;
  }

  @Override
  public int getNanos() {
      return (int)((instant.getMillis() % 1000) * 1000000);
  }
}
