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

package com.google.cloud.trace.sdk.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.cloud.trace.sdk.AlwaysTraceEnablingPolicy;
import com.google.cloud.trace.sdk.TraceEnablingPolicy;
import com.google.cloud.trace.sdk.TraceHeaders;
import com.google.cloud.trace.sdk.TraceIdGenerator;
import com.google.cloud.trace.sdk.TraceSpanDataHandle;
import com.google.cloud.trace.sdk.TraceWriter;
import com.google.cloud.trace.sdk.servlet.TraceRequestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

/**
 * Tests for the {@link TraceRequestUtils} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class TraceRequestUtilsTest {

  private static final String PROJECT_ID = "project id";
  private static final String TRACE_ID = "trace id";
  private static final String NEW_TRACE_ID = "new trace id";
  private static final long SPAN_ID = 5;
  private static final String URI = "myuri";
  private static final String QUERY = "a=5";
  
  @Mock private HttpServletRequest request;
  @Mock private TraceWriter writer;
  private TraceRequestUtils utils;
  
  @Before
  public void setUp() {
    utils = new TraceRequestUtils();
    utils.traceIdGenerator = new TraceIdGenerator() {
      @Override
      public String generate() {
        return NEW_TRACE_ID;
      }
    };
  }
  
  @Test
  public void testCreateRequestSpanDataExistingTrace() {
    setUpMockRequest(TRACE_ID, SPAN_ID, true);
    TraceSpanDataHandle handle = utils.createRequestSpanData(writer,
        request, PROJECT_ID);
    assertEquals(PROJECT_ID, handle.getSpanData().getProjectId());
    assertEquals(TRACE_ID, handle.getSpanData().getTraceId());
    assertEquals(SPAN_ID, handle.getSpanData().getParentSpanId());
    assertEquals(URI + "?" + QUERY, handle.getSpanData().getName());
    assertFalse(handle.getSpanData().getShouldWrite());
    Mockito.verify(request).setAttribute(TraceRequestUtils.TRACE_SPAN_DATA_ATTRIBUTE,
        handle);
  }
  
  @Test
  public void testCreateRequestSpanDataNonDefaultEnabling() {
    setUpMockRequest(TRACE_ID, SPAN_ID, true);
    utils.enablingPolicy = new AlwaysTraceEnablingPolicy();
    TraceSpanDataHandle handle = utils.createRequestSpanData(writer,
        request, PROJECT_ID);
    assertTrue(handle.getSpanData().getShouldWrite());
  }
  
  @Test
  public void testCreateRequestSpanDataNewTrace() {
    setUpMockRequest(null, null, false);
    TraceSpanDataHandle handle = utils.createRequestSpanData(writer,
        request, PROJECT_ID);
    assertEquals(PROJECT_ID, handle.getSpanData().getProjectId());
    assertEquals(NEW_TRACE_ID, handle.getSpanData().getTraceId());
    assertEquals(0, handle.getSpanData().getParentSpanId());
    assertFalse(handle.getSpanData().getShouldWrite());
    assertEquals(URI + "?" + QUERY, handle.getSpanData().getName());
  }
  
  @Test
  public void testCreateRequestSpanDataNewTraceDontEnable() {
    setUpMockRequest(TRACE_ID, SPAN_ID, true);
    utils.enablingPolicy = new TraceEnablingPolicy() {      
      @Override
      public boolean isTracingEnabled(boolean alreadyEnabled) {
        return false;
      }
    };
    TraceSpanDataHandle handle = utils.createRequestSpanData(writer,
        request, PROJECT_ID);
    assertFalse(handle.getSpanData().getShouldWrite());
  }
  
  @Test
  public void testInitFromProperties() {
    Properties props = new Properties();
    props.setProperty(TraceRequestUtils.class.getName() + ".enablingPolicy",
        "com.google.cloud.trace.sdk.AlwaysTraceEnablingPolicy");
    utils.initFromProperties(props);
    assertTrue(utils.enablingPolicy instanceof AlwaysTraceEnablingPolicy);
  }
  
  private void setUpMockRequest(String traceId, Long spanId, boolean enabled) {
    Mockito.when(request.getRequestURI()).thenReturn(URI);
    Mockito.when(request.getQueryString()).thenReturn(QUERY);
    if (traceId != null) {
      Mockito.when(request.getHeader(TraceHeaders.TRACE_ID_HEADER)).thenReturn(traceId);
    }
    if (spanId != null) {
      Mockito.when(request.getHeader(TraceHeaders.TRACE_SPAN_ID_HEADER)).thenReturn(
          spanId.toString());
    }
    Mockito.when(request.getHeader(TraceHeaders.TRACE_ENABLED_HEADER)).thenReturn(
        Boolean.toString(enabled));
  }
}
