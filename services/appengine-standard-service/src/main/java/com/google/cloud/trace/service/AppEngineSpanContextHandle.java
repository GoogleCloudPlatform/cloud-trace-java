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

package com.google.cloud.trace.service;

import com.google.appengine.api.labs.trace.Span;
import com.google.apphosting.api.CloudTraceContext;
import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanContextHandle;
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import com.google.common.base.Throwables;
import com.google.common.primitives.UnsignedLong;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protos.cloud.trace.TraceId.TraceIdProto;
import java.math.BigInteger;

/**
 * Implementation of {@link SpanContextHandle} based on Google App Engine's Trace
 * Service API.
 */
class AppEngineSpanContextHandle implements SpanContextHandle {
  private final Span span;

  AppEngineSpanContextHandle(Span span) {
    this.span = span;
  }

  @Override
  public SpanContext getCurrentSpanContext() {
    return getSpanContext(span.getContext());
  }

  @Override
  public void detach() {}

  Span getSpan() {
    return span;
  }

  private static SpanContext getSpanContext(CloudTraceContext cloudTraceContext) {
    if (cloudTraceContext == null) {
      return new SpanContext(TraceId.invalid(), SpanId.invalid(), new TraceOptions());
    }

    try {
      // Extract the trace ID from the binary protobuf CloudTraceContext#traceId.
      TraceIdProto traceIdProto = TraceIdProto.parseFrom(cloudTraceContext.getTraceId());
      BigInteger traceIdHi = UnsignedLong.fromLongBits(traceIdProto.getHi()).bigIntegerValue();
      BigInteger traceIdLo = UnsignedLong.fromLongBits(traceIdProto.getLo()).bigIntegerValue();
      BigInteger traceId = traceIdHi.shiftLeft(64).or(traceIdLo);

      return new SpanContext(new TraceId(traceId), new SpanId(cloudTraceContext.getSpanId()),
          new TraceOptions((int) cloudTraceContext.getTraceMask()));
    } catch (InvalidProtocolBufferException e) {
      throw Throwables.propagate(e);
    }
  }
}
