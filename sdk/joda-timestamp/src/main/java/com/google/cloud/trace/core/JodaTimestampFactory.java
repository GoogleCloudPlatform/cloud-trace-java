package com.google.cloud.trace.core;

import org.joda.time.Instant;

public class JodaTimestampFactory implements TimestampFactory {
  @Override
  public Timestamp now() {
    return new JodaTimestamp(Instant.now());
  }
}
