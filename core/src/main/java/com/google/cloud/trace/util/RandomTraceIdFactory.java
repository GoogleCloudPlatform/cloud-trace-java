package com.google.cloud.trace.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomTraceIdFactory extends AbstractTraceIdFactory {
  private static final int TRACE_ID_BIT_LENGTH = 128;

  private final SecureRandom random;

  public RandomTraceIdFactory() {
    this.random = new SecureRandom();
  }

  public RandomTraceIdFactory(byte[] seed) {
    this.random = new SecureRandom(seed);
  }

  public RandomTraceIdFactory(SecureRandom random) {
    this.random = random;
  }

  @Override
  public TraceId nextId() {
    return new TraceId(new BigInteger(TRACE_ID_BIT_LENGTH, random));
  }
}
