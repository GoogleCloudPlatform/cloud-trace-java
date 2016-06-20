package com.google.cloud.trace.util;

import java.math.BigInteger;

public abstract class AbstractTraceIdFactory implements IdFactory<TraceId> {
  @Override
  public abstract TraceId nextId();

  @Override
  public TraceId invalid() {
    return new TraceId(BigInteger.ZERO);
  }
}
