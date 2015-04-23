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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigInteger;

/**
 * Tests for the {@link TraceSpanData} class.
 */
@RunWith(JUnit4.class)
public class TraceSpanDataTest {
  private static final String TRACE_NAME = "name";
  private static final String TRACE_ID = "trace id";

  @Before
  public void setUp() {
    ThreadTraceContext.clear();
  }
  
  @Test
  public void testCreate() {
    TraceSpanData.clock = new FakeClock();
    TraceSpanData span = new TraceSpanData(TRACE_ID, TRACE_NAME, BigInteger.ZERO,
        true);
    assertEquals(FakeClock.DEFAULT_MILLIS, span.getStartTimeMillis());
    assertEquals(BigInteger.ZERO, span.getParentSpanId());
    assertEquals(span.getContext(), ThreadTraceContext.peek());
  }
  
  @Test
  public void testAddLabel() {
    TraceSpanData span = new TraceSpanData(TRACE_ID, TRACE_NAME, BigInteger.ZERO,
        true);
    span.addLabel("key", "value");
    assertTrue(span.getLabelMap().containsKey("key"));
    assertEquals("value", span.getLabelMap().get("key").getValue());
  }
  
  @Test
  public void testCreateChildSpanData() {
    TraceSpanData span = new TraceSpanData(TRACE_ID, TRACE_NAME, BigInteger.ZERO,
        true);
    TraceSpanData child = span.createChildSpanData("child");
    assertEquals(child.getParentSpanId(), span.getSpanId());
    assertEquals(child.getContext(), ThreadTraceContext.peek());
    child.close();
    assertEquals(span.getContext(), ThreadTraceContext.peek());
    span.close();
    assertTrue(ThreadTraceContext.isEmpty());
  }
  
  @Test
  public void testClose() {
    TraceSpanData span = new TraceSpanData(TRACE_ID, TRACE_NAME, BigInteger.ZERO,
        true);
    span.close();
    assertTrue(ThreadTraceContext.isEmpty());
  }
}
