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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for the {@link TraceSpanData} class.
 */
@RunWith(JUnit4.class)
public class TraceSpanDataTest {
  private static final String TRACE_NAME = "name";
  private static final String TRACE_ID = "trace id";

  private TraceSpanDataBuilder builder;
  
  @Before
  public void setUp() {
    builder = new DefaultTraceSpanDataBuilder(TRACE_ID, TRACE_NAME);
  }
  
  @Test
  public void testCreate() {
    TraceSpanData span = new TraceSpanData(builder);
    assertEquals(0, span.getStartTimeMillis());
    assertEquals(BigInteger.ZERO, span.getParentSpanId());
    assertTrue(span.getLabelMap().isEmpty());
  }
  
  @Test
  public void testCreateIncludingLabel() {
    TraceSpanDataBuilder builder = new DefaultTraceSpanDataBuilder(TRACE_ID, TRACE_NAME) {
      @Override
      public Map<String, TraceSpanLabel> getLabelMap() {
        Map<String, TraceSpanLabel> labelMap = new HashMap<>();
        labelMap.put("somekey", new TraceSpanLabel("labelkey", "labelvalue"));
        return labelMap;
      }
    };
    TraceSpanData span = new TraceSpanData(builder);
    assertEquals(0, span.getStartTimeMillis());
    assertEquals(BigInteger.ZERO, span.getParentSpanId());
    assertEquals(1, span.getLabelMap().size());
    assertTrue(span.getLabelMap().containsKey("somekey"));
    assertEquals("labelkey", span.getLabelMap().get("somekey").getKey());
    assertEquals("labelvalue", span.getLabelMap().get("somekey").getValue());
  }
  
  @Test
  public void testAddLabel() {
    TraceSpanData span = new TraceSpanData(builder);
    span.addLabel("key", "value");
    assertTrue(span.getLabelMap().containsKey("key"));
    assertEquals("value", span.getLabelMap().get("key").getValue());
  }
  
  @Test
  public void testStart() {
    TraceSpanData.clock = new FakeClock();
    TraceSpanData span = new TraceSpanData(builder);
    span.start();
    assertEquals(FakeClock.DEFAULT_MILLIS, span.getStartTimeMillis());
  }
  
  @Test
  public void testEnd() {
    TraceSpanData span = new TraceSpanData(builder);
    span.start();
    span.end();
  }
  
  @Test
  public void testEndBeforeStart() {
    TraceSpanData span = new TraceSpanData(builder);
    try {
      span.end();
    } catch (IllegalStateException ise) {
      return;
    }
    fail("Didn't catch expected exception");
  }
  
  @Test
  public void testIsEnded() {
    TraceSpanData span = new TraceSpanData(builder);
    assertFalse(span.isEnded());
    span.start();
    span.end();
    assertTrue(span.isEnded());
  }
}
