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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;

/**
 * Tests for the {@link TraceSpanDataHandle} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class TraceSpanDataHandleTest {
  
  private static final String CHILD_NAME = "childName";
  private static final String TRACE_NAME = "name";
  private static final String TRACE_ID = "traceId";
  private static final String PROJECT_ID = "projectId";

  @Mock private TraceWriter writer;
  private FakeClock fakeClock = new FakeClock();
  
  @Before
  public void setUp() {
    TraceSpanData.clock = fakeClock;
  }
  
  @Test
  public void testCreateAndAutoClose() throws CloudTraceException {
    TraceSpanData innerSpanData;
    try (TraceSpanDataHandle handle =
        new TraceSpanDataHandle(writer, PROJECT_ID, TRACE_ID, TRACE_NAME, BigInteger.ZERO,
            true)) {
      innerSpanData = handle.getSpanData();
      assertEquals(PROJECT_ID, innerSpanData.getProjectId());
      assertEquals(FakeClock.DEFAULT_MILLIS, innerSpanData.getStartTimeMillis());
      assertEquals(0, innerSpanData.getEndTimeMillis());
      assertEquals(TRACE_NAME, innerSpanData.getName());
      fakeClock.setMillis(777);
    }
    assertEquals(777, innerSpanData.getEndTimeMillis());
    Mockito.verify(writer).writeSpan(innerSpanData);
  }
  
  @Test
  public void testCreateChildSpanDataHandle() {
    TraceSpanDataHandle parent =
        new TraceSpanDataHandle(writer, PROJECT_ID, TRACE_ID, TRACE_NAME, BigInteger.ZERO,
            true);
    TraceSpanDataHandle child = parent.createChildSpanDataHandle(CHILD_NAME);
    assertEquals(CHILD_NAME, child.getSpanData().getName());
    assertEquals(TRACE_ID, child.getSpanData().getTraceId());
    assertEquals(PROJECT_ID, child.getSpanData().getProjectId());
    assertEquals(parent.getSpanData().getSpanId(), child.getSpanData().getParentSpanId());
    parent.close();
  }
}
