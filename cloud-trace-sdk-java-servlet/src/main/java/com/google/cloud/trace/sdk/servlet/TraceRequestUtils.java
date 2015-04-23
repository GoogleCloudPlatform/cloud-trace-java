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
import com.google.cloud.trace.sdk.TraceEnablingPolicy;
import com.google.cloud.trace.sdk.TraceHeaders;
import com.google.cloud.trace.sdk.TraceIdGenerator;
import com.google.cloud.trace.sdk.TraceSpanData;
import com.google.cloud.trace.sdk.TraceSpanDataHandle;
import com.google.cloud.trace.sdk.TraceSpanLabel;
import com.google.cloud.trace.sdk.TraceWriter;
import com.google.cloud.trace.sdk.ReflectionUtils;
import com.google.cloud.trace.sdk.UUIDTraceIdGenerator;

import java.math.BigInteger;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilities for working with {@link TraceSpanData}s in the context of servlet requests.
 */
public class TraceRequestUtils implements CanInitFromProperties {

  private static final Logger logger = Logger.getLogger(TraceRequestUtils.class.getName());

  /**
   * Generator for new trace id's when we don't see one on the request. Package-scoped for testing
   * purposes.
   */
  TraceIdGenerator traceIdGenerator = new UUIDTraceIdGenerator();

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
   * Looks for a trace id header already on the request and generates a new one if it can't find
   * anything.
   */
  private String getTraceId(HttpServletRequest request) {
    String traceId = request.getHeader(TraceHeaders.TRACE_ID_HEADER);
    if (traceId == null) {
      // Let's create one.
      traceId = traceIdGenerator.generate();
    }
    return traceId;
  }

  /**
   * Looks for a parent span id already existing on the request headers.
   */
  private BigInteger getParentSpanId(HttpServletRequest request) {
    String parentSpanId = request.getHeader(TraceHeaders.TRACE_SPAN_ID_HEADER);
    if (parentSpanId != null) {
      try {
        return new BigInteger(parentSpanId);
      } catch (NumberFormatException nfe) {
        logger.log(Level.WARNING,
            "Found non-numeric span id in the headers (" + parentSpanId + ")");
      }
    }
    return BigInteger.ZERO;
  }

  /**
   * Looks for the enabled flag value on the request headers.
   */
  private boolean getTraceEnabledHeader(HttpServletRequest request) {
    String enabledHeader = request.getHeader(TraceHeaders.TRACE_ENABLED_HEADER);
    if (enabledHeader != null) {
      try {
        return Boolean.parseBoolean(enabledHeader);
      } catch (NumberFormatException nfe) {
        logger.log(Level.WARNING,
            "Found non-boolean enabled flag in the headers (" + enabledHeader + ")");
      }
    }
    return false;
  }

  /**
   * Creates a new {@link TraceSpanDataHandle}. If a trace and span are already on the servlet
   * request, it parents the new {@link TraceSpanData} appropriately and assigns it to the given
   * trace. If not, it creates things from scratch.
   */
  public TraceSpanDataHandle createRequestSpanData(TraceWriter writer,
      HttpServletRequest request) {
    TraceSpanDataHandle spanDataHandle = new TraceSpanDataHandle(writer,
        getTraceId(request),
        getFullURL(request),
        getParentSpanId(request),
        enablingPolicy.isTracingEnabled(getTraceEnabledHeader(request)));
    request.setAttribute(TRACE_SPAN_DATA_ATTRIBUTE, spanDataHandle);

    // Let's add a standard label.
    TraceSpanLabel httpMethodLabel =
        new TraceSpanLabel(HttpServletSpanLabels.HTTP_METHOD_LABEL_KEY, request.getMethod());
    spanDataHandle.getSpanData().addLabel(httpMethodLabel);

    return spanDataHandle;
  }

  /**
   * Gets the trace span data handle off the given servlet request.
   */
  public TraceSpanDataHandle getRequestSpanDataHandle(HttpServletRequest req) {
    return (TraceSpanDataHandle) req.getAttribute(TRACE_SPAN_DATA_ATTRIBUTE);
  }

  /**
   * Helper method to get the servlet request URL in the format we use for trace id's started by
   * requests.
   */
  private String getFullURL(HttpServletRequest request) {
    String requestURL = request.getRequestURI();
    String queryString = request.getQueryString();

    if (queryString == null) {
      return requestURL.toString();
    } else {
      return requestURL + '?' + queryString;
    }
  }
}
