// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.sdk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for the {@link TraceSpanData} class.
 */
@RunWith(JUnit4.class)
public class TraceSpanDataTest {
  private static final String TRACE_NAME = "name";
  private static final String PROJECT_ID = "project id";
  private static final String TRACE_ID = "trace id";

  @Test
  public void testCreate() {
    TraceSpanData.clock = new FakeClock();
    TraceSpanData span = new TraceSpanData(PROJECT_ID, TRACE_ID, TRACE_NAME, 0);
    assertEquals(FakeClock.DEFAULT_MILLIS, span.getStartTimeMillis());
    assertEquals(0, span.getParentSpanId());
  }
  
  @Test
  public void testCreateChildSpanData() {
    TraceSpanData span = new TraceSpanData(PROJECT_ID, TRACE_ID, TRACE_NAME, 0);
    TraceSpanData child = span.createChildSpanData("child");
    assertEquals(child.getParentSpanId(), span.getSpanId());
  }
  
  @Test
  public void testClose() {
    TraceSpanData span = new TraceSpanData(PROJECT_ID, TRACE_ID, TRACE_NAME, 0);
    span.close();
  }
}
