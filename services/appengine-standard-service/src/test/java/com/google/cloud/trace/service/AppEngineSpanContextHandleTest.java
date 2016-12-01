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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.appengine.api.labs.trace.Span;
import com.google.apphosting.api.CloudTraceContext;
import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanContextHandle;
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import java.math.BigInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit tests for {@link AppEngineSpanContextHandle}.
 */
@RunWith(JUnit4.class)
public class AppEngineSpanContextHandleTest {
  private final Span mockSpan = mock(Span.class);

  @Test
  public void getSpan() {
    AppEngineSpanContextHandle handle = new AppEngineSpanContextHandle(mockSpan);

    Span span = handle.getSpan();

    assertThat(span).isSameAs(mockSpan);
  }

  @Test
  public void getCurrent() {
    CloudTraceContext cloudTraceContext = new CloudTraceContext(
        // Protobuf-encoded upper and lower 64 bits of the example trace ID
        // 7ae1c6346b9cf9a272cb6504b5a10dcc/123456789.
        new byte[] {(byte) 0x09, (byte) 0xa2, (byte) 0xf9, (byte) 0x9c, (byte) 0x6b, (byte) 0x34,
            (byte) 0xc6, (byte) 0xe1, (byte) 0x7a, (byte) 0x11, (byte) 0xcc, (byte) 0x0d,
            (byte) 0xa1, (byte) 0xb5, (byte) 0x04, (byte) 0x65, (byte) 0xcb, (byte) 0x72},
        123456789L,
        // Trace enabled.
        1L);
    when(mockSpan.getContext()).thenReturn(cloudTraceContext);
    SpanContextHandle handle = new AppEngineSpanContextHandle(mockSpan);

    SpanContext current = handle.getCurrentSpanContext();

    assertThat(current).isEqualTo(
        new SpanContext(new TraceId(new BigInteger("7ae1c6346b9cf9a272cb6504b5a10dcc", 16)),
            new SpanId(123456789L), new TraceOptions(1)));
  }

  @Test
  public void getCurrent_noContext() {
    when(mockSpan.getContext()).thenReturn(null);
    SpanContextHandle handle = new AppEngineSpanContextHandle(mockSpan);

    SpanContext current = handle.getCurrentSpanContext();

    assertThat(current).isEqualTo(
        new SpanContext(TraceId.invalid(), SpanId.invalid(), new TraceOptions()));
  }
}
