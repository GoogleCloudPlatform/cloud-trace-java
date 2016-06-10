package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;

import java.math.BigInteger;
import java.util.Objects;

public class TraceId {
  private static final int TRACE_ID_BIT_LENGTH = 128;

  private final BigInteger traceId;

  public TraceId(BigInteger traceId) {
    this.traceId = traceId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof TraceId)) {
      return false;
    }

    TraceId that = (TraceId)obj;
    return Objects.equals(traceId, that.traceId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(traceId);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("traceId", String.format("%032x", traceId))
        .toString();
  }

  public boolean isValid() {
    return (traceId.signum() > 0) && (traceId.bitLength() <= TRACE_ID_BIT_LENGTH);
  }

  public BigInteger getTraceId() {
    return traceId;
  }
}
