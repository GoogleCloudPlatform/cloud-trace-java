// Copyright 2016 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
