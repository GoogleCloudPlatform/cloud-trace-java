package com.google.cloud.trace.util;

public class ConstantTraceOptionsFactory implements TraceOptionsFactory {
  private final TraceOptions traceOptions;

  public ConstantTraceOptionsFactory(boolean traceEnabled, boolean stackTraceEnabled) {
    this.traceOptions = new TraceOptions()
        .overrideTraceEnabled(traceEnabled).overrideStackTraceEnabled(stackTraceEnabled);
  }

  @Override
  public TraceOptions create() {
    return traceOptions;
  }

  @Override
  public TraceOptions create(TraceOptions traceOptions) {
    return traceOptions;
  }
}
