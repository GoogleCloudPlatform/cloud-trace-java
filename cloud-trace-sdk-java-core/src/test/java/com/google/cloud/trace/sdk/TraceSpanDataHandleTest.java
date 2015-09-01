// Copyright 2015 Google Inc. All rights reserved.
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for the {@link TraceSpanDataHandle} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class TraceSpanDataHandleTest {
  @Mock
  private TraceWriter mockWriter;
  private TraceSpanDataBuilder builder;

  @Before
  public void setUp() {
    builder = new DefaultTraceSpanDataBuilder(null, null);
  }

  @Test
  public void testCreate() throws Exception {
    TraceSpanDataHandle handle = new TraceSpanDataHandle(builder, mockWriter);
    assertTrue(handle.getSpanData().isStarted());
    assertFalse(handle.getSpanData().isEnded());
    handle.close();
    assertTrue(handle.getSpanData().isEnded());
    verify(mockWriter, times(1)).writeSpan(handle.getSpanData());
  }

  @Test
  public void testAutoClosable() throws Exception {
    TraceSpanData spanData = null;
    try (TraceSpanDataHandle handle = new TraceSpanDataHandle(builder, mockWriter)) {
      assertTrue(handle.getSpanData().isStarted());
      // Stash this for testing (although it's generally expected that users won't
      // access it outside the try block).
      spanData = handle.getSpanData();
    }
    assertTrue(spanData.isEnded());
    verify(mockWriter, times(1)).writeSpan(spanData);
  }

  @Test
  public void testCreateChild() throws Exception {
    TraceSpanDataHandle handle = new TraceSpanDataHandle(builder, mockWriter);
    TraceSpanDataHandle childHandle = handle.createChild("CHILD");
    assertEquals("CHILD", childHandle.getSpanData().getName());
    assertEquals(
        handle.getSpanData().getContext().getSpanId(), childHandle.getSpanData().getParentSpanId());

    childHandle.close();
    assertTrue(childHandle.getSpanData().isEnded());

    handle.close();
    assertTrue(handle.getSpanData().isEnded());
    verify(mockWriter, times(1)).writeSpan(childHandle.getSpanData());
    verify(mockWriter, times(1)).writeSpan(handle.getSpanData());
  }

  /**
   * This test is largely for demonstration purposes -- the previous tests should
   * really cover all the functionality.
   */
  @Test
  public void testNestedAutoClosable() throws Exception {
    // Stash span data's for testing (although it's generally expected that users won't
    // access it outside the try block).
    TraceSpanData spanData = null;
    TraceSpanData childSpanData = null;
    try (TraceSpanDataHandle handle = new TraceSpanDataHandle(builder, mockWriter)) {
      spanData = handle.getSpanData();
      try (TraceSpanDataHandle childHandle = handle.createChild("CHILD")) {
        childSpanData = childHandle.getSpanData();
      }
    }

    assertEquals(spanData.getContext().getSpanId(), childSpanData.getParentSpanId());
    assertTrue(spanData.isEnded());
    assertTrue(childSpanData.isEnded());
    verify(mockWriter, times(1)).writeSpan(spanData);
    verify(mockWriter, times(1)).writeSpan(childSpanData);
  }
}
