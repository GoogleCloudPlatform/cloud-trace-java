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

import com.google.cloud.trace.sdk.ThreadTraceContextTraceSpanDataListener;
import com.google.cloud.trace.sdk.TraceContext;
import com.google.cloud.trace.sdk.TraceSpanData;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;

import javax.annotation.Nullable;

/**
 * Tests for the {@link AppEngineTraceSpanDataListener} class.
 */
public class AppEngineTraceSpanDataListenerTest {
  private static final String TRACE_ID = "1";
  private static final BigInteger SPAN_ID = new BigInteger("11");
  private static final String SPAN_NAME_1 = "span1";
  private static final String SPAN_NAME_2 = "span2";

  private static final TraceContext defaultContext =
      new TraceContext(TRACE_ID, SPAN_ID, TraceContext.TRACE_ENABLED); 

  private static final AppEngineCurrentTraceContext currentContext =
      new AppEngineCurrentTraceContext() {
    private TraceContext current;

    @Override
    public void set(@Nullable TraceContext context) {
      current = context;
    }

    @Override
    @Nullable
    public TraceContext get() {
      return current;
    }
  };

  @BeforeClass
  public static void setUpClass() {
    AppEngineCurrentTraceContext.setInstance(currentContext);
    TraceSpanData.setListener(new AppEngineTraceSpanDataListener(
        new ThreadTraceContextTraceSpanDataListener()));
  }

  @AfterClass
  public static void tearDownClass() {
    TraceSpanData.setListener(null);
  }

  @Before
  public void setUp() {
  }

  @Test
  public void testNullContext() {
    currentContext.set(null);
    assertEquals(null, currentContext.get());

    TraceSpanData span1 = new TraceSpanData(
        AppEngineTraceSpanDataBuilderFactory.getBuilder(SPAN_NAME_1));
    span1.start();
    // span1 is a root node.
    assertEquals(BigInteger.ZERO, span1.getParentSpanId());
    assertEquals(span1.getContext(), currentContext.get());

    span1.end();
    assertEquals(null, currentContext.get());
  }

  @Test
  public void testDefaultContext() {
    currentContext.set(defaultContext);
    assertEquals(defaultContext, currentContext.get());

    TraceSpanData span1 = new TraceSpanData(
        AppEngineTraceSpanDataBuilderFactory.getBuilder(SPAN_NAME_1));
    span1.start();
    // span1 is a child of DEFAULT.
    assertEquals(defaultContext.getSpanId(), span1.getParentSpanId());
    assertEquals(defaultContext.getTraceId(), span1.getContext().getTraceId());
    assertEquals(defaultContext.getOptions(), span1.getContext().getOptions());
    assertEquals(span1.getContext(), currentContext.get());

    span1.end();
    assertEquals(null, currentContext.get());
  }

  @Test
  public void testNestedSpans() {
    currentContext.set(defaultContext);
    assertEquals(defaultContext, currentContext.get());

    TraceSpanData span1 = new TraceSpanData(
        AppEngineTraceSpanDataBuilderFactory.getBuilder(SPAN_NAME_1));
    span1.start();
    // span1 is a child of DEFAULT.
    assertEquals(SPAN_ID, span1.getParentSpanId());
    assertEquals(span1.getContext(), currentContext.get());

    TraceSpanData span2 = new TraceSpanData(
        AppEngineTraceSpanDataBuilderFactory.getBuilder(SPAN_NAME_2));
    span2.start();
    // span2 is a child of span1.
    assertEquals(span1.getContext().getSpanId(), span2.getParentSpanId());
    assertEquals(span2.getContext(), currentContext.get());

    span2.end();
    assertEquals(span1.getContext(), currentContext.get());

    span1.end();
    assertEquals(null, currentContext.get());
  }
}