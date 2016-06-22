package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.UnsignedLongs;

import java.util.Objects;

public class SpanId {
  private final long spanId;

  public SpanId(long spanId) {
    this.spanId = spanId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof SpanId)) {
      return false;
    }

    SpanId that = (SpanId)obj;
    return spanId == that.spanId;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(spanId);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("spanId", UnsignedLongs.toString(spanId))
        .toString();
  }

  public boolean isValid() {
    return spanId != 0;
  }

  public long getSpanId() {
    return spanId;
  }
}
