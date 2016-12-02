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

import com.google.cloud.trace.TestTracer.AnnotateEvent;
import com.google.cloud.trace.TestTracer.EndSpanEvent;
import com.google.cloud.trace.TestTracer.StackTraceEvent;
import com.google.cloud.trace.TestTracer.StartSpanEvent;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import java.math.BigInteger;
import org.junit.Before;
import org.junit.Test;

public class TestTracerTest {
  private static final SpanContext testSpanContext = new SpanContext(
      new TraceId(BigInteger.valueOf(1234)),
      new SpanId(5932),
      new TraceOptions(7));
  private static final TraceContext testTraceContext = new TraceContext(
      new TestSpanContextHandle(testSpanContext));

  private TestTracer tracer;

  @Before
  public void setup() {
    tracer = new TestTracer();
  }

  @Test
  public void testStartSpan() throws Exception {
    tracer.startSpan("test-span");
    assertThat(tracer.startSpanEvents).hasSize(1);
    StartSpanEvent startSpanEvent = tracer.startSpanEvents.get(0);
    assertThat(startSpanEvent.getName()).isEqualTo("test-span");
    assertThat(startSpanEvent.getOptions()).isNull();
    assertThat(startSpanEvent.getTraceContext()).isNotNull();

    tracer.startSpan("test-span2");
    assertThat(tracer.startSpanEvents).hasSize(2);
    StartSpanEvent startSpanEvent2 = tracer.startSpanEvents.get(1);
    assertThat(startSpanEvent2.getTraceContext()).isNotEqualTo(startSpanEvent);
  }

  @Test
  public void endSpan() throws Exception {
    tracer.endSpan(testTraceContext);
    assertThat(tracer.endSpanEvents).hasSize(1);
    EndSpanEvent endSpanEvent = tracer.endSpanEvents.get(0);
    assertThat(endSpanEvent.getTraceContext()).isEqualTo(testTraceContext);
    assertThat(endSpanEvent.getEndSpanOptions()).isNull();
  }

  @Test
  public void annotateSpan() throws Exception {
    Labels labels = Labels.builder().add("key1", "value1").add("key2", "value2").build();
    tracer.annotateSpan(testTraceContext, labels);
    assertThat(tracer.annotateEvents).hasSize(1);
    AnnotateEvent annotateEvent = tracer.annotateEvents.get(0);
    assertThat(annotateEvent.getTraceContext()).isEqualTo(testTraceContext);
    assertThat(annotateEvent.getLabels()).isEqualTo(labels);
  }

  @Test
  public void setStackTrace() throws Exception {
    StackTrace stackTrace = StackTrace.builder().add("testClass", "testMethod", "testFile", 1, 2).build();
    tracer.setStackTrace(testTraceContext, stackTrace);
    assertThat(tracer.stackTraceEvents).hasSize(1);
    StackTraceEvent stackTraceEvent = tracer.stackTraceEvents.get(0);
    assertThat(stackTraceEvent.getTraceContext()).isEqualTo(testTraceContext);
    assertThat(stackTraceEvent.getStackTrace()).isEqualTo(stackTrace);
  }

  @Test
  public void testReset() throws Exception {
    TraceContext traceContext = tracer.startSpan("test");
    tracer.annotateSpan(traceContext, Labels.builder().build());
    tracer.setStackTrace(traceContext, StackTrace.builder().build());
    tracer.endSpan(traceContext);
    tracer.reset();
    assertThat(tracer.stackTraceEvents).hasSize(0);
    assertThat(tracer.annotateEvents).hasSize(0);
    assertThat(tracer.stackTraceEvents).hasSize(0);
    assertThat(tracer.endSpanEvents).hasSize(0);
  }
}
