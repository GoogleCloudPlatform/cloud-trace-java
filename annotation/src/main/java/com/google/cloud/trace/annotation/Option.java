package com.google.cloud.trace.annotation;

public enum Option {
  DEFAULT(null),
  TRUE(true),
  FALSE(false);

  private final Boolean value;

  Option(Boolean value) {
    this.value = value;
  }

  public Boolean getBooleanValue() {
    return value;
  }
}
