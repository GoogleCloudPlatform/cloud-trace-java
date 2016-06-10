package com.google.cloud.trace.util;

import java.security.SecureRandom;

public class RandomSpanIdFactory extends AbstractSpanIdFactory {
  private static final int SPAN_ID_BIT_LENGTH = 64;

  private final SecureRandom random;

  public RandomSpanIdFactory() {
    this.random = new SecureRandom();
  }

  public RandomSpanIdFactory(byte[] seed) {
    this.random = new SecureRandom(seed);
  }

  public RandomSpanIdFactory(SecureRandom random) {
    this.random = random;
  }

  @Override
  public SpanId nextId() {
    return new SpanId(random.nextLong());
  }
}
