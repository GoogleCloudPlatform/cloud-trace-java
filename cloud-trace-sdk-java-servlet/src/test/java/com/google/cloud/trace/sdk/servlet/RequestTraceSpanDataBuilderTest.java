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

package com.google.cloud.trace.sdk.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.cloud.trace.sdk.AbstractTraceSpanDataBuilder;
import com.google.cloud.trace.sdk.ForwardTraceEnablingPolicy;
import com.google.cloud.trace.sdk.TraceContext;
import com.google.cloud.trace.sdk.TraceIdGenerator;
import com.google.cloud.trace.sdk.TraceWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

/**
 * Tests for the {@link RequestTraceSpanDataBuilder} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestTraceSpanDataBuilderTest {
  private static final String TRACE_ID = "trace id";
  private static final String NEW_TRACE_ID = "new trace id";
  private static final BigInteger SPAN_ID = BigInteger.valueOf(5);
  private static final String URI = "myuri";
  private static final String QUERY = "a=5";

  @Mock
  private HttpServletRequest request;

  @Mock
  private TraceWriter writer;

  @Before
  public void setUp() {
    AbstractTraceSpanDataBuilder.traceIdGenerator = new TraceIdGenerator() {
      @Override
      public String generate() {
        return NEW_TRACE_ID;
      }
    };
  }

  @Test
  public void testExistingTrace() {
    setUpMockRequest(TRACE_ID, SPAN_ID, true);
    RequestTraceSpanDataBuilder builder =
        new RequestTraceSpanDataBuilder(request, new ForwardTraceEnablingPolicy());
    assertEquals(TRACE_ID, builder.getTraceContext().getTraceId());
    assertEquals(SPAN_ID, builder.getParentSpanId());
    assertEquals(URI + "?" + QUERY, builder.getName());
    assertTrue(builder.getTraceContext().getShouldWrite());
  }

  @Test
  public void testNewTrace() {
    setUpMockRequest(null, null, false);
    RequestTraceSpanDataBuilder builder =
        new RequestTraceSpanDataBuilder(request, new ForwardTraceEnablingPolicy());
    assertEquals(NEW_TRACE_ID, builder.getTraceContext().getTraceId());
    assertEquals(BigInteger.ZERO, builder.getParentSpanId());
    assertFalse(builder.getTraceContext().getShouldWrite());
    assertEquals(URI + "?" + QUERY, builder.getName());
  }

  @Test
  public void testCustomNamingStrategy() {
    setUpMockRequest(null, null, false);
    RequestTraceSpanDataBuilder builder =
        new RequestTraceSpanDataBuilder(request, new ForwardTraceEnablingPolicy(),
            new RequestTraceSpanNamingStrategy() {
              @Override
              public String getName(HttpServletRequest request) {
                return "ZZZ" + request.getRequestURI();
              }
            });
    assertEquals("ZZZ" + URI, builder.getName());    
  }
  
  private void setUpMockRequest(String traceId, BigInteger spanId, boolean enabled) {
    Mockito.when(request.getRequestURI()).thenReturn(URI);
    Mockito.when(request.getQueryString()).thenReturn(QUERY);
    TraceContext incomingContext = new TraceContext(traceId, spanId, enabled ? 1 : 0);
    if (traceId != null) {
      Mockito.when(request.getHeader(TraceContext.TRACE_HEADER)).thenReturn(incomingContext.toTraceHeader());
    } else {
      Mockito.when(request.getHeader(TraceContext.TRACE_HEADER)).thenReturn(null);
    }
  }
}
