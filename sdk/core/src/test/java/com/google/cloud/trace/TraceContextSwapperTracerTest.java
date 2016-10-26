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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.trace.util.EndSpanOptions;
import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanId;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.StartSpanOptions;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceId;
import com.google.cloud.trace.util.TraceOptions;
import java.math.BigInteger;
import org.junit.Before;
import org.junit.Test;

public class TraceContextSwapperTracerTest {

  private Tracer fakeTracer;
  private TraceContext root1;
  private TraceContext root2;
  private TraceContext newContext1;
  private TraceContext newContext2;
  private TraceContextHandler handler1;
  private TraceContextHandler handler2;
  private TraceContextSwapper swapper;
  private TraceContextSwapperTracer tracer;

  @Before
  public void setup() {
    fakeTracer = mock(Tracer.class);
    root1 = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(4), TraceOptions.forTraceEnabled());
    root2 = new TraceContext(new TraceId(BigInteger.valueOf(4)), new SpanId(5), TraceOptions.forTraceDisabled());
    newContext1 = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(6), TraceOptions.forTraceEnabled());
    newContext2 = new TraceContext(new TraceId(BigInteger.valueOf(4)), new SpanId(6), TraceOptions.forTraceEnabled());

    handler1 = new DefaultTraceContextHandler(root1);
    handler2 = new DefaultTraceContextHandler(root2);
    swapper = new TestContextSwapper();
    swapper.swap(handler1);
    tracer = new TraceContextSwapperTracer(fakeTracer, swapper);
  }

  @Test
  public void testStartAndEndSpan() throws Exception {

    // Start a new span
    when(fakeTracer.startSpan(root1, "foo")).thenReturn(newContext1);
    tracer.startSpan("foo");
    assertThat(handler1.current()).isSameAs(newContext1);
    assertThat(tracer.getCurrentTraceContext()).isSameAs(handler1.current());
    verify(fakeTracer).startSpan(root1, "foo");

    // Swap the context handler
    swapper.swap(handler2);

    // Start a new span
    when(fakeTracer.startSpan(root2, "bar")).thenReturn(newContext2);
    tracer.startSpan("bar");
    assertThat(handler2.current()).isSameAs(newContext2);
    assertThat(tracer.getCurrentTraceContext()).isSameAs(handler2.current());
    verify(fakeTracer).startSpan(root2, "bar");

    // End the span
    tracer.endSpan();
    assertThat(handler2.current()).isSameAs(root2);
    assertThat(tracer.getCurrentTraceContext()).isSameAs(handler2.current());
    verify(fakeTracer).endSpan(newContext2);

    // Swap the context handler again
    swapper.swap(handler1);

    // End the span
    tracer.endSpan();
    assertThat(handler1.current()).isSameAs(root1);
    assertThat(tracer.getCurrentTraceContext()).isSameAs(handler1.current());
    verify(fakeTracer).endSpan(newContext1);
  }

  @Test
  public void testStartAndEndSpanWithOptions() throws Exception {
    StartSpanOptions startOptions;
    // Start a new span
    startOptions = new StartSpanOptions();
    when(fakeTracer.startSpan(root1, "foo", startOptions)).thenReturn(newContext1);
    tracer.startSpan("foo", startOptions);
    assertThat(handler1.current()).isSameAs(newContext1);
    verify(fakeTracer).startSpan(same(root1), eq("foo"), same(startOptions));

    // Swap the context handler
    swapper.swap(handler2);

    // Start a new span
    startOptions = new StartSpanOptions();
    when(fakeTracer.startSpan(root2, "bar", startOptions)).thenReturn(newContext2);
    tracer.startSpan("bar", startOptions);
    assertThat(handler2.current()).isSameAs(newContext2);
    verify(fakeTracer).startSpan(same(root2), eq("bar"), same(startOptions));

    EndSpanOptions endOptions;
    // End the span
    endOptions = new EndSpanOptions();
    tracer.endSpan(endOptions);
    assertThat(handler2.current()).isSameAs(root2);
    verify(fakeTracer).endSpan(same(newContext2), same(endOptions));

    // Swap the context handler again
    swapper.swap(handler1);

    // End the span
    endOptions = new EndSpanOptions();
    tracer.endSpan(endOptions);
    assertThat(handler1.current()).isSameAs(root1);
    verify(fakeTracer).endSpan(same(newContext1), same(endOptions));
  }

  @Test
  public void annotateSpan() throws Exception {
    Labels labels1 = Labels.builder().add("key1", "value1").build();
    Labels labels2 = Labels.builder().add("key2", "value2").build();

    // Annotate span
    tracer.annotateSpan(labels1);
    verify(fakeTracer).annotateSpan(same(root1), same(labels1));

    // Swap the context handler
    swapper.swap(handler2);

    // Annotate span
    tracer.annotateSpan(labels2);
    verify(fakeTracer).annotateSpan(same(root2), same(labels2));
  }

  @Test
  public void setStackTrace() throws Exception {
    StackTrace stackTrace1 = StackTrace.builder().build();
    StackTrace stackTrace2 = StackTrace.builder().build();

    // Annotate span
    tracer.setStackTrace(stackTrace1);
    verify(fakeTracer).setStackTrace(same(root1), same(stackTrace1));

    // Swap the context handler
    swapper.swap(handler2);

    // Annotate span
    tracer.setStackTrace(stackTrace2);
    verify(fakeTracer).setStackTrace(same(root2), same(stackTrace2));

  }

  private static class TestContextSwapper implements TraceContextSwapper {
    private TraceContextHandler handler;

    private TestContextSwapper() {
      this.handler = null;
    }

    @Override
    public TraceContextHandler swap(TraceContextHandler newHandler) {
      TraceContextHandler previous = handler;
      handler = newHandler;
      return previous;
    }

    @Override
    public TraceContextHandler currentHandler() {
      return handler;
    }
  }
}