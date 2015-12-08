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

package com.google.cloud.trace.sdk.gae;

import static org.junit.Assert.*;

import com.google.cloud.trace.sdk.AbstractTraceSpanDataBuilder;
import com.google.cloud.trace.sdk.SpanIdGenerator;
import com.google.cloud.trace.sdk.TraceContext;
import com.google.cloud.trace.sdk.TraceIdGenerator;
import com.google.cloud.trace.sdk.TraceSpanDataBuilder;

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;

import javax.annotation.Nullable;

/**
 * Tests for the {@link AppEngineTraceSpanDataBuilderFactory} class.
 */
public class AppEngineTraceSpanDataBuilderFactoryTest {
  private static final String EVERY_TRACE_ID = "1";
  private static final BigInteger EVERY_SPAN_ID = new BigInteger("11");
  private static final String TRACE_ID = "2";
  private static final BigInteger SPAN_ID = new BigInteger("22");
  private static final String SPAN_NAME = "span";

  @BeforeClass
  public static void init() {
    AbstractTraceSpanDataBuilder.traceIdGenerator = new TraceIdGenerator() {
      @Override
      public String generate() {
        return EVERY_TRACE_ID;
      }
    };
    AbstractTraceSpanDataBuilder.spanIdGenerator = new SpanIdGenerator() {
      @Override
      public BigInteger generate() {
        return EVERY_SPAN_ID;
      }
    };
  }

  @Test
  public void testConstructorParentContext() {
    AppEngineCurrentTraceContext current = new AppEngineCurrentTraceContext() {
      @Override
      @Nullable
      public TraceContext get() {
        return new TraceContext(TRACE_ID, SPAN_ID, TraceContext.TRACE_ENABLED);
      }
    };
    AppEngineCurrentTraceContext.setInstance(current);
    TraceSpanDataBuilder builder = AppEngineTraceSpanDataBuilderFactory.getBuilder(SPAN_NAME);
    assertEquals(SPAN_ID, builder.getParentSpanId());
    assertEquals(TRACE_ID, builder.getTraceContext().getTraceId());
    assertEquals(EVERY_SPAN_ID, builder.getTraceContext().getSpanId());
    assertEquals(TraceContext.TRACE_ENABLED, builder.getTraceContext().getOptions());
    assertEquals(SPAN_NAME, builder.getName());
  }

  @Test
  public void testConstructorNullParentContext() {
    AppEngineCurrentTraceContext current = new AppEngineCurrentTraceContext() {
      @Override
      @Nullable
      public TraceContext get() {
        return null;
      }
    };
    AppEngineCurrentTraceContext.setInstance(current);
    TraceSpanDataBuilder builder = AppEngineTraceSpanDataBuilderFactory.getBuilder(SPAN_NAME);
    assertEquals(BigInteger.ZERO, builder.getParentSpanId());
    assertEquals(EVERY_TRACE_ID, builder.getTraceContext().getTraceId());
    assertEquals(EVERY_SPAN_ID, builder.getTraceContext().getSpanId());
    assertEquals(TraceContext.TRACE_OPTIONS_NONE, builder.getTraceContext().getOptions());
    assertEquals(SPAN_NAME, builder.getName());
  }
}