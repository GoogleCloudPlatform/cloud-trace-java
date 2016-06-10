package com.google.cloud.trace.util;

import java.time.Instant;

public class JavaTimestampFactory implements TimestampFactory {
  @Override
  public Timestamp now() {
    return new JavaTimestamp(Instant.now());
  }
}
