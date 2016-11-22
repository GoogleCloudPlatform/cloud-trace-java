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
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.core.EndSpanOptions;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.TraceContext;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AppEngineTracer}.
 */
@RunWith(JUnit4.class)
public class AppEngineTracerTest {
  private final com.google.appengine.api.labs.trace.TraceService mockTraceService =
      mock(com.google.appengine.api.labs.trace.TraceService.class);
  private final Span mockSpan = mock(Span.class);
  private final Tracer tracer = new AppEngineTracer(mockTraceService);

  @Test
  public void startSpan() {
    when(mockTraceService.startSpan("test span")).thenReturn(mockSpan);

    TraceContext traceContext = tracer.startSpan("test span");

    assertThat(traceContext).isNotNull();
    verify(mockTraceService).startSpan("test span");
    verifyNoMoreInteractions(mockTraceService, mockSpan);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void startSpan_withOptions() {
    tracer.startSpan("test span", new StartSpanOptions());
  }

  @Test
  public void endSpan() {
    when(mockTraceService.startSpan("test span")).thenReturn(mockSpan);

    TraceContext traceContext = tracer.startSpan("test span");
    tracer.endSpan(traceContext);

    verify(mockSpan).endSpan();
    verifyNoMoreInteractions(mockSpan);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void endSpan_withOptions() {
    when(mockTraceService.startSpan("test span")).thenReturn(mockSpan);

    TraceContext traceContext = tracer.startSpan("test span");
    tracer.endSpan(traceContext, new EndSpanOptions());
  }

  @Test
  public void annotateSpan() {
    when(mockTraceService.startSpan("test span")).thenReturn(mockSpan);

    TraceContext traceContext = tracer.startSpan("test span");
    tracer.annotateSpan(
        traceContext, Labels.builder().add("key1", "value1").add("key2", "value2").build());

    verify(mockSpan).setLabel("key1", "value1");
    verify(mockSpan).setLabel("key2", "value2");
    verifyNoMoreInteractions(mockSpan);
  }
}
