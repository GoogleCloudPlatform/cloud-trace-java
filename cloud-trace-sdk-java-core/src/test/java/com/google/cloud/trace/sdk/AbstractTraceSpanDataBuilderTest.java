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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Tests for the {@link AbstractTraceSpanDataBuilder} class.
 */
public class AbstractTraceSpanDataBuilderTest {

  private static final BigInteger PARENT_SPAN_ID = new BigInteger("55");
  private static final String TRACE_ID = "trace id";
  private static final String CHILD_SPAN_NAME = "child";
  
  private AbstractTraceSpanDataBuilder builder;
  
  @Before
  public void setUp() {
   builder = new AbstractTraceSpanDataBuilder() {
    @Override
    public TraceContext getTraceContext() {
      return new TraceContext(TRACE_ID, PARENT_SPAN_ID, TraceContext.TRACE_ENABLED);
    }

    @Override
    public String getName() {
      return "AbstractTraceSpanDataBuilder";
    }

    @Override
    public BigInteger getParentSpanId() {
      return BigInteger.ZERO;
    }
   };
  }
  
  @Test
  public void testCreateChild() {
    TraceSpanDataBuilder childBuilder = builder.createChild(CHILD_SPAN_NAME);
    assertEquals(CHILD_SPAN_NAME, childBuilder.getName());
    assertEquals(PARENT_SPAN_ID, childBuilder.getParentSpanId());
    assertEquals(TRACE_ID, childBuilder.getTraceContext().getTraceId());
    assertTrue(childBuilder.getTraceContext().getShouldWrite());
  }
}

