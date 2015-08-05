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

package com.google.cloud.trace.sdk.servlet;

import com.google.cloud.trace.sdk.CanInitFromProperties;
import com.google.cloud.trace.sdk.NeverTraceEnablingPolicy;
import com.google.cloud.trace.sdk.ReflectionUtils;
import com.google.cloud.trace.sdk.TraceEnablingPolicy;
import com.google.cloud.trace.sdk.TraceSpanData;
import com.google.cloud.trace.sdk.TraceSpanDataBuilder;
import com.google.cloud.trace.sdk.TraceSpanLabel;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilities for working with {@link TraceSpanData}s in the context of servlet requests.
 */
public class TraceRequestUtils implements CanInitFromProperties {

  /**
   * The name we give to the trace span data when it is placed in a servlet request attribute.
   */
  static final String TRACE_SPAN_DATA_ATTRIBUTE = "com.google.cloud.trace.trace_span_data";

  /**
   * The trace-enabling policy we use when creating request trace contexts. Package-scoped for
   * testing purposes.
   */
  TraceEnablingPolicy enablingPolicy = new NeverTraceEnablingPolicy();


  /**
   * Initializes/reinitializes based on values in the given properties file.
   */
  @Override
  public void initFromProperties(Properties props) {
    String enablingPolicyClassName = props.getProperty(getClass().getName() + ".enablingPolicy");
    if (enablingPolicyClassName != null && !enablingPolicyClassName.isEmpty()) {
      enablingPolicy = (TraceEnablingPolicy) ReflectionUtils.createFromProperties(
          enablingPolicyClassName, props);
    }
  }

  /**
   * Creates a new {@link TraceSpanData}. If a trace and span are already on the servlet
   * request, it parents the new {@link TraceSpanData} appropriately and assigns it to the given
   * trace. If not, it creates things from scratch.
   */
  public TraceSpanData createRequestSpanData(HttpServletRequest request) {
    TraceSpanDataBuilder spanDataBuilder = new RequestTraceSpanDataBuilder(request, enablingPolicy);
    TraceSpanData spanData = new TraceSpanData(spanDataBuilder);
    request.setAttribute(TRACE_SPAN_DATA_ATTRIBUTE, spanData);

    // Let's add a standard label.
    TraceSpanLabel httpMethodLabel =
        new TraceSpanLabel(HttpServletSpanLabels.HTTP_METHOD_LABEL_KEY, request.getMethod());
    spanData.addLabel(httpMethodLabel);
    spanData.start();

    return spanData;
  }

  /**
   * Gets the trace span data off the given servlet request.
   */
  public TraceSpanData getRequestSpanDataHandle(HttpServletRequest req) {
    return (TraceSpanData) req.getAttribute(TRACE_SPAN_DATA_ATTRIBUTE);
  }
}
