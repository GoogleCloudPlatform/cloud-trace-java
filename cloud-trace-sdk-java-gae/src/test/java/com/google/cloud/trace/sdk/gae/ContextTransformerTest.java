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

import static org.junit.Assert.*;

import com.google.apphosting.api.CloudTraceContext;
import com.google.cloud.trace.sdk.TraceContext;
import com.google.cloud.trace.sdk.gae.TraceId.TraceIdProto;

import org.junit.Test;

import java.math.BigInteger;

/**
 * Tests for the {@link ContextTransformer} class.
 */
public class ContextTransformerTest {
  private static final String TRACE_ID = "0000000000000001ffffffffffffffff";
  private static final TraceIdProto TRACE_ID_PROTO =
      TraceIdProto.newBuilder().setHi(1).setLo(-1).build();
  private static final BigInteger SPAN_ID = new BigInteger("11");

  @Test
  public void testCloudTraceContextToTraceContext() {
    CloudTraceContext input = new CloudTraceContext(TRACE_ID_PROTO.toByteArray(),
        SPAN_ID.longValue(), TraceContext.TRACE_ENABLED);
    TraceContext output = ContextTransformer.transform(input);
    assertEquals(TRACE_ID, output.getTraceId());
    assertEquals(SPAN_ID, output.getSpanId());
    assertEquals(TraceContext.TRACE_ENABLED, output.getOptions());
  }

  @Test
  public void testTraceContextToCloudTraceContext() {
    TraceContext input = new TraceContext(TRACE_ID, SPAN_ID, TraceContext.TRACE_ENABLED);
    CloudTraceContext output = ContextTransformer.transform(input);
    assertArrayEquals(TRACE_ID_PROTO.toByteArray(), output.getTraceId());
    assertEquals(SPAN_ID.longValue(), output.getSpanId());
    assertEquals(TraceContext.TRACE_ENABLED, output.getTraceMask());
  }
}