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

package com.google.cloud.trace;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.trace.core.EndSpanOptions;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanContextHandler;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.TraceContext;
import org.junit.Test;

public class TraceTest {
  @Test
  public void testGetTracer_No() {
    ManagedTracer tracer = Trace.getTracer();
    TraceContext context = tracer.startSpan("hello");
    assertInvalid(context.getCurrent());
    tracer.annotateSpan(context, Labels.builder().build());
    tracer.setStackTrace(context, StackTrace.builder().build());
    tracer.endSpan(context);

    context = tracer.startSpan("hello", new StartSpanOptions());
    assertInvalid(context.getCurrent());
    tracer.endSpan(context, new EndSpanOptions());

    assertInvalid(tracer.getCurrentSpanContext());
  }

  @Test
  public void testGetSpanContextHandler_No() {
    SpanContextHandler handler = Trace.getSpanContextHandler();
    assertInvalid(handler.current());
    SpanContext attached = handler.attach(handler.current());
    assertInvalid(attached);
    assertInvalid(handler.current());
    handler.detach(attached);
    assertInvalid(handler.current());
  }

  private void assertInvalid(SpanContext context) {
    assertThat(context.getTraceId().isValid()).isFalse();
    assertThat(context.getSpanId().isValid()).isFalse();
    assertThat(context.getTraceOptions().getOptionsMask()).isEqualTo(0);
  }
}
