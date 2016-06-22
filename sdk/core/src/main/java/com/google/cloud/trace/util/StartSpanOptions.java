package com.google.cloud.trace.util;

public class StartSpanOptions {
  private Timestamp timestamp;
  private SpanKind spanKind;
  private TraceOptions traceOptions;

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public StartSpanOptions setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public SpanKind getSpanKind() {
    return spanKind;
  }

  public StartSpanOptions setSpanKind(SpanKind spanKind) {
    this.spanKind = spanKind;
    return this;
  }

  public TraceOptions getTraceOptions() {
    return traceOptions;
  }

  public StartSpanOptions setTraceOptions(TraceOptions traceOptions) {
    this.traceOptions = traceOptions;
    return this;
  }
}
