package com.google.cloud.trace.util;

import java.security.SecureRandom;
import java.util.Random;

public class NaiveSamplingTraceOptionsFactory implements TraceOptionsFactory {
  private final Random random;
  private final double samplingRate;
  private final boolean stackTraceEnabled;

  public NaiveSamplingTraceOptionsFactory(
      Random random, double samplingRate, boolean stackTraceEnabled) {
    this.random = random;
    this.samplingRate = samplingRate;
    this.stackTraceEnabled = stackTraceEnabled;
  }

  public NaiveSamplingTraceOptionsFactory(double samplingRate, boolean stackTraceEnabled) {
    this(new SecureRandom(), samplingRate, stackTraceEnabled);
  }

  @Override
  public TraceOptions create() {
    boolean traceEnabled = random.nextDouble() <= samplingRate;
    return new TraceOptions()
        .overrideTraceEnabled(traceEnabled).overrideStackTraceEnabled(stackTraceEnabled);
  }

  @Override
  public TraceOptions create(TraceOptions traceOptions) {
    return traceOptions;
  }
}
