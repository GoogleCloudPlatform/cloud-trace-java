package com.google.cloud.trace.util;

public abstract class AbstractSpanIdFactory implements IdFactory<SpanId> {
  @Override
  public abstract SpanId nextId();

  @Override
  public SpanId invalid() {
    return new SpanId(0);
  }
}
