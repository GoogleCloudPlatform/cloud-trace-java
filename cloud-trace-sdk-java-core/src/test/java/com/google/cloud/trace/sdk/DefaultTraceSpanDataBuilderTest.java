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

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Tests for the {@link DefaultTraceSpanDataBuilder} class.
 */
public class DefaultTraceSpanDataBuilderTest {
  private static final BigInteger EVERY_SPAN_ID = new BigInteger("222");
  private static final BigInteger OTHER_SPAN_ID = new BigInteger("444");
  private static final BigInteger PARENT_SPAN_ID = new BigInteger("333");
  private static final String EVERY_TRACE_ID = "111";
  private static final String SPAN_NAME = "George";

  @BeforeClass
  public static void init() {
    AbstractTraceSpanDataBuilder.spanIdGenerator = new SpanIdGenerator() {
      @Override
      public BigInteger generate() {
        return EVERY_SPAN_ID;
      }
    };

    AbstractTraceSpanDataBuilder.traceIdGenerator = new TraceIdGenerator() {
      @Override
      public String generate() {
        return EVERY_TRACE_ID;
      }
    };
  }

  @Test
  public void testCreateNewTraceCtor() {
    DefaultTraceSpanDataBuilder builder = new DefaultTraceSpanDataBuilder(SPAN_NAME);
    assertEquals(SPAN_NAME, builder.getName());
    assertEquals(EVERY_TRACE_ID, builder.getTraceContext().getTraceId());
    assertEquals(EVERY_SPAN_ID, builder.getTraceContext().getSpanId());
    assertEquals(TraceContext.TRACE_ENABLED, builder.getTraceContext().getOptions());
	assertEquals(0, builder.getStartTimeMillis());
	assertEquals(0, builder.getEndTimeMillis());
  }

  @Test
  public void testUseExistingTraceCtor() {
    DefaultTraceSpanDataBuilder builder = new DefaultTraceSpanDataBuilder("zzz", SPAN_NAME);
    assertEquals(SPAN_NAME, builder.getName());
    assertEquals("zzz", builder.getTraceContext().getTraceId());
    assertEquals(EVERY_SPAN_ID, builder.getTraceContext().getSpanId());
    assertEquals(TraceContext.TRACE_ENABLED, builder.getTraceContext().getOptions());
	assertEquals(0, builder.getStartTimeMillis());
	assertEquals(0, builder.getEndTimeMillis());
  }

  @Test
  public void testHasExistingContextCtor() {
    TraceContext context = new TraceContext("zzz", OTHER_SPAN_ID, TraceContext.TRACE_ENABLED);
    DefaultTraceSpanDataBuilder builder =
        new DefaultTraceSpanDataBuilder(context, SPAN_NAME, PARENT_SPAN_ID);
    assertEquals(SPAN_NAME, builder.getName());
    assertEquals("zzz", builder.getTraceContext().getTraceId());
    assertEquals(OTHER_SPAN_ID, builder.getTraceContext().getSpanId());
    assertEquals(TraceContext.TRACE_ENABLED, builder.getTraceContext().getOptions());
  }

  @Test
  public void testExplicitOptioinsCtor() {
    DefaultTraceSpanDataBuilder builder =
        new DefaultTraceSpanDataBuilder(TraceContext.TRACE_OPTIONS_NONE, SPAN_NAME);
    assertEquals(SPAN_NAME, builder.getName());
    assertEquals(EVERY_TRACE_ID, builder.getTraceContext().getTraceId());
    assertEquals(EVERY_SPAN_ID, builder.getTraceContext().getSpanId());
    assertEquals(TraceContext.TRACE_OPTIONS_NONE, builder.getTraceContext().getOptions());
  }

  @Test
  public void testExplicitOptioinsExistingTraceCtor() {
    DefaultTraceSpanDataBuilder builder = new DefaultTraceSpanDataBuilder(
        "zzz", OTHER_SPAN_ID, TraceContext.TRACE_OPTIONS_NONE, SPAN_NAME);
    assertEquals(SPAN_NAME, builder.getName());
    assertEquals("zzz", builder.getTraceContext().getTraceId());
    assertEquals(EVERY_SPAN_ID, builder.getTraceContext().getSpanId());
    assertEquals(TraceContext.TRACE_OPTIONS_NONE, builder.getTraceContext().getOptions());
  }
}