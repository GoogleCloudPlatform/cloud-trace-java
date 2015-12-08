// Copyright 2015 Google Inc. All rights reserved.
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

package com.google.cloud.trace.sdk.gae;

import com.google.apphosting.api.CloudTraceContext;
import com.google.cloud.trace.sdk.TraceContext;
import com.google.cloud.trace.sdk.gae.TraceId.TraceIdProto;
import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Transforms between two forms of trace context:
 *     com.google.apphosting.api.CloudTraceContext
 *     com.google.cloud.trace.sdk.TraceContext
 */
public final class ContextTransformer {
  private static final Logger logger = Logger.getLogger(ContextTransformer.class.getName());
  private static final int HEX = 16;

  @Nullable
  public static TraceContext transform(@Nullable CloudTraceContext context) {
    if (context == null) {
      return null;
    }
    String traceId = traceIdBytesToString(context.getTraceId());
    if (traceId == null) {
      return null;
    }

    // Using this instead of BigInteger.valueOf(long) to ensure positive span IDs.
    // Cloud Trace API doesn't accept negative span IDs.
    BigInteger spanId = new BigInteger(Long.toHexString(context.getSpanId()), HEX);
    TraceContext result = new TraceContext(traceId, spanId, context.getTraceMask());
    return result;
  }

  @Nullable
  public static CloudTraceContext transform(@Nullable TraceContext context) {
    if (context == null) {
      return null;
    }
    return new CloudTraceContext(traceIdStringToBytes(context.getTraceId()),
        context.getSpanId().longValue(), context.getOptions());
  }

  @Nullable
  private static String traceIdBytesToString(byte[] traceId) {
    try {
      TraceIdProto proto = TraceIdProto.parseFrom(traceId);
      return String.format("%016x%016x", proto.getHi(), proto.getLo());
    } catch (InvalidProtocolBufferException e) {
      logger.log(Level.SEVERE, "Failed to parse trace id: ", traceId);
      return null;
    }
  }

  private static byte[] traceIdStringToBytes(String traceId) {
    TraceIdProto.Builder builder = TraceIdProto.newBuilder();
    Long hi = new BigInteger(traceId.substring(0, 16), 16).longValue();
    Long lo = new BigInteger(traceId.substring(16, 32), 16).longValue();
    return builder.setHi(hi).setLo(lo).build().toByteArray();
  }
}