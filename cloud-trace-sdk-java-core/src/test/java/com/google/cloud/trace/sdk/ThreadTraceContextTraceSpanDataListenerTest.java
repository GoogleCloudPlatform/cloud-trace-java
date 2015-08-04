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

package com.google.cloud.trace.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Tests for the {@link ThreadTraceContextTraceSpanDataListener} class.
 */
public class ThreadTraceContextTraceSpanDataListenerTest {

  private static final String TRACE_NAME = "name";
  private static final String TRACE_ID = "trace id";

  @BeforeClass
  public static void setUpClass() {
    TraceSpanData.setListener(new ThreadTraceContextTraceSpanDataListener());
  }
  
  @AfterClass
  public static void tearDownClass() {
    TraceSpanData.setListener(null);
  }
  
  @Test
  public void testSpanLifecycle() {
    assertTrue(ThreadTraceContext.isEmpty());
    TraceSpanData span1 = new TraceSpanData(TRACE_ID, TRACE_NAME, BigInteger.ZERO,
        true);
    span1.start();
    assertFalse(ThreadTraceContext.isEmpty());
    assertEquals(span1.getContext(), ThreadTraceContext.peek());
    
    TraceSpanData span2 = new TraceSpanData(TRACE_ID + "2", TRACE_NAME + "2", BigInteger.ZERO,
        true);
    span2.start();
    assertEquals(span2.getContext(), ThreadTraceContext.peek());
    
    span2.end();
    assertEquals(span1.getContext(), ThreadTraceContext.peek());

    span1.end();
    assertTrue(ThreadTraceContext.isEmpty());
  }
}
