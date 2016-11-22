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

import com.google.cloud.trace.Tracer;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit tests for {@link AppEngineTraceService}.
 */
@RunWith(JUnit4.class)
public class AppEngineTraceServiceTest {
  private final com.google.appengine.api.labs.trace.TraceService mockTraceService =
      mock(com.google.appengine.api.labs.trace.TraceService.class);
  private final TraceService traceService = new AppEngineTraceService(mockTraceService);

  @Test
  public void getTracer() {
    Tracer tracer = traceService.getTracer();

    assertThat(tracer).isNotNull();
    assertThat(tracer).isInstanceOf(AppEngineTracer.class);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getSpanContextHandler() {
    traceService.getSpanContextHandler();
  }
}
