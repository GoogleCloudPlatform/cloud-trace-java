package com.google.cloud.trace.util;

public class EndSpanOptions {
  private Timestamp timestamp;

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public EndSpanOptions setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
    return this;
  }
}
