package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class TraceContext {
  private final TraceId traceId;
  private final SpanId spanId;
  private final TraceOptions traceOptions;

  public TraceContext(TraceId traceId, SpanId spanId, TraceOptions traceOptions) {
    this.traceId = traceId;
    this.spanId = spanId;
    this.traceOptions = traceOptions;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof TraceContext)) {
      return false;
    }

    TraceContext that = (TraceContext)obj;
    return Objects.equals(traceId, that.traceId)
        && Objects.equals(spanId, that.spanId)
        && Objects.equals(traceOptions, that.traceOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(traceId, spanId, traceOptions);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("traceId", traceId)
        .add("spanId", spanId)
        .add("traceOptions", traceOptions)
        .toString();
  }

  public TraceId getTraceId() {
    return traceId;
  }

  public SpanId getSpanId() {
    return spanId;
  }

  public TraceOptions getTraceOptions() {
    return traceOptions;
  }

  public TraceContext overrideOptions(TraceOptions overrideOptions) {
    return new TraceContext(traceId, spanId, overrideOptions);
  }
}
