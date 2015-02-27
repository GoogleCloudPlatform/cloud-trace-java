// Copyright 2014 Google Inc. All rights reserved.
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
import static org.mockito.Mockito.when;

import com.google.api.client.http.GenericUrl;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.Properties;

/**
 * Tests for the {@link CloudTraceWriter} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudTraceWriterTest {

  private static final String PROJECT_ID = "19";
  private static final String TRACEID_1 = "20";
  private static final String TRACEID_2 = "21";

  @Mock private CloudTraceRequestFactory mockRequestFactory;
  @Mock private CloudTraceResponse mock200Response;
  @Mock private CloudTraceRequest mockRequest;

  private CloudTraceWriter writer;
  private ObjectMapper objectMapper = new ObjectMapper();

  @Before
  public void setUp() {
    writer = new CloudTraceWriter();
    writer.setApiEndpoint("http://localhost/");
    writer.setProjectId(PROJECT_ID);
    writer.setRequestFactory(mockRequestFactory);
    when(mock200Response.getStatusCode()).thenReturn(200);
  }

  @Test
  public void testInitFromProperties() throws Exception {
    CloudTraceWriter writer = new CloudTraceWriter();
    Properties props = new Properties();
    props.setProperty(CloudTraceWriter.class.getName() + ".projectId", "19");
    props.setProperty(CloudTraceWriter.class.getName() + ".apiEndpoint", "http://localhost:8888/");
    writer.initFromProperties(props);
    assertEquals("19", writer.getProjectId());
    assertEquals("http://localhost:8888/", writer.getApiEndpoint());
  }

  @Test
  public void testWriteSpansAggregation() throws Exception {
    TraceSpanData span1 = new TraceSpanData(PROJECT_ID, TRACEID_1, "span1", BigInteger.ZERO, true);
    TraceSpanData span2 = new TraceSpanData(PROJECT_ID, TRACEID_1, "span2", BigInteger.ZERO, true);
    TraceSpanData span3 = new TraceSpanData(PROJECT_ID, TRACEID_2, "span3", BigInteger.ZERO, true);

    ArgumentCaptor<GenericUrl> urlCapture = ArgumentCaptor.forClass(GenericUrl.class);
    ArgumentCaptor<String> bodyCapture = ArgumentCaptor.forClass(String.class);
    when(mockRequestFactory.buildPatchRequest(urlCapture.capture(), bodyCapture.capture()))
        .thenReturn(mockRequest);
    when(mockRequestFactory.execute(mockRequest)).thenReturn(mock200Response);

    writer.writeSpans(span1, span2, span3);
    
    assertEquals(2, urlCapture.getAllValues().size());
    
    // Don't care about the order of calls to the API.
    int firstIdx, secondIdx;
    if (urlCapture.getAllValues().get(0).toString().endsWith(TRACEID_1)) {
      firstIdx = 0;
      secondIdx = 1;
    } else {
      firstIdx = 1;
      secondIdx = 0;
    }
    
    GenericUrl firstUrl = urlCapture.getAllValues().get(firstIdx);
    String firstBody = bodyCapture.getAllValues().get(firstIdx);
    assertEquals("http://localhost/v1/projects/19/traces/20", firstUrl.toString());
    JsonNode trace = objectMapper.readValue(firstBody, JsonNode.class);
    assertEquals(PROJECT_ID, trace.get("projectId").getTextValue());
    assertEquals(TRACEID_1, trace.get("traceId").getTextValue());
    assertEquals(2, trace.get("span").size());
    assertEquals("span1", trace.get("span").get(0).get("name").getTextValue());
    assertEquals("span2", trace.get("span").get(1).get("name").getTextValue());
    
    GenericUrl secondUrl = urlCapture.getAllValues().get(secondIdx);
    String secondBody = bodyCapture.getAllValues().get(secondIdx);
    assertEquals("http://localhost/v1/projects/19/traces/21", secondUrl.toString());
    JsonNode trace2 = objectMapper.readValue(secondBody, JsonNode.class);
    assertEquals(PROJECT_ID, trace2.get("projectId").getTextValue());
    assertEquals(TRACEID_2, trace2.get("traceId").getTextValue());
    assertEquals(1,trace2.get("span").size());
    assertEquals("span3", trace2.get("span").get(0).get("name").getTextValue());
  }
}
