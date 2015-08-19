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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.math.BigInteger;

/**
 * Tests for the {@link TraceContext} class.
 */
public class TraceContextTest {

  @Test
  public void testShouldWrite() {
    TraceContext context = new TraceContext("1", BigInteger.valueOf(2), TraceContext.TRACE_ENABLED);
    assertTrue(context.getShouldWrite());
    assertEquals(TraceContext.TRACE_ENABLED, context.getOptions());
    
    context.setShouldWrite(false);
    assertFalse(context.getShouldWrite());
    assertEquals(0, context.getOptions());

    context = new TraceContext("1", BigInteger.valueOf(2), TraceContext.TRACE_OPTIONS_NONE);
    assertFalse(context.getShouldWrite());
    assertEquals(TraceContext.TRACE_OPTIONS_NONE, context.getOptions());
  }
  
  @Test
  public void testParseCompleteContext() {
    String headerStr = "55abc/33;o=9";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    assertEquals("55abc", context.getTraceId());
    assertEquals(BigInteger.valueOf(33), context.getSpanId());
    assertEquals(9, context.getOptions());
    assertEquals(headerStr, context.toTraceHeader());
  }

  @Test
  public void testParseCompleteContextWithTrailingStuff() {
    String headerStr = "55abc/33;o=9;a=b";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    assertEquals("55abc", context.getTraceId());
    assertEquals(BigInteger.valueOf(33), context.getSpanId());
    assertEquals(9, context.getOptions());
    assertEquals(headerStr, context.toTraceHeader());
  }

  @Test
  public void testParseNoOptions() {
    String headerStr = "55abc/33";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    assertEquals("55abc", context.getTraceId());
    assertEquals(BigInteger.valueOf(33), context.getSpanId());
    assertEquals(0, context.getOptions());
    assertEquals(headerStr, context.toTraceHeader());
  }

  @Test
  public void testParseNoOptionsWithTrailingStuff() {
    String headerStr = "55abc/33;a=b";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    assertEquals("55abc", context.getTraceId());
    assertEquals(BigInteger.valueOf(33), context.getSpanId());
    assertEquals(0, context.getOptions());
    assertEquals(headerStr, context.toTraceHeader());
  }

  @Test
  public void testParseNoSpanOrOptions() {
    String headerStr = "55abc";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    assertEquals(headerStr, context.getTraceId());
    assertNull(context.getSpanId());
    assertEquals(0, context.getOptions());
    assertEquals(headerStr + "/", context.toTraceHeader());
  }

  @Test
  public void testParseNoSpanTrailingSlash() {
    String headerStr = "55abc/";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    assertEquals("55abc", context.getTraceId());
    assertNull(context.getSpanId());
    assertEquals(0, context.getOptions());
    assertEquals(headerStr, context.toTraceHeader());
  }

  @Test
  public void testParseNoSpanTrailingOptions() {
    String headerStr = "55abc;o=9";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    verifyEmpty(context);
  }

  @Test
  public void testParseInvalidSpan() {
    String headerStr = "55abc/ZZZ;o=9";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    assertEquals("55abc", context.getTraceId());
    assertNull(context.getSpanId());
    assertEquals(9, context.getOptions());
    assertEquals("55abc/;o=9", context.toTraceHeader());
  }

  @Test
  public void testParseInvalidOptions() {
    String headerStr = "55abc/33;o=ZZZ";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    assertEquals("55abc", context.getTraceId());
    assertEquals(BigInteger.valueOf(33), context.getSpanId());
    assertEquals(0, context.getOptions());
    assertEquals("55abc/33", context.toTraceHeader());
  }

  @Test
  public void testOptionsMustPrecedeKeyValues() {
    String headerStr = "55abc/33;q=yyy;o=55";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    assertEquals("55abc", context.getTraceId());
    assertEquals(BigInteger.valueOf(33), context.getSpanId());
    assertEquals(0, context.getOptions());
    assertEquals("55abc/33;q=yyy", context.toTraceHeader());
  }
  
  @Test
  public void testKeyValues() {
    String headerStr = "55abc/33;o=9;q=YYY;x=BBB";
    TraceContext context = TraceContext.fromTraceHeader(headerStr);
    assertEquals("55abc", context.getTraceId());
    assertEquals(BigInteger.valueOf(33), context.getSpanId());
    assertEquals(9, context.getOptions());
    assertEquals(2, context.getData().size());
    assertEquals("YYY", context.getData().get("q"));
    assertEquals("BBB", context.getData().get("x"));
    assertEquals(headerStr, context.toTraceHeader());
  }
  
  private void verifyEmpty(TraceContext context) {
    assertNull(context.getSpanId());
    assertEquals(0, context.getOptions());
    assertTrue(context.getData().isEmpty());
  }
}
