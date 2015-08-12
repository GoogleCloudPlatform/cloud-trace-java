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

package com.google.cloud.trace.sdk.servlet;

import com.google.cloud.trace.sdk.AbstractTraceSpanDataBuilder;
import com.google.cloud.trace.sdk.AlwaysTraceEnablingPolicy;
import com.google.cloud.trace.sdk.SpanIdGenerator;
import com.google.cloud.trace.sdk.TraceContext;
import com.google.cloud.trace.sdk.TraceEnablingPolicy;
import com.google.cloud.trace.sdk.TraceHeaders;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Builds trace span data based on an http servlet request.
 */
public class RequestTraceSpanDataBuilder extends AbstractTraceSpanDataBuilder {

  private static final Logger logger = Logger.getLogger(RequestTraceSpanDataBuilder.class.getName());

  private final HttpServletRequest request;
  private final TraceEnablingPolicy enablingPolicy;

  /**
   * Creates a trace span data builder for the given servlet request with the
   * given enabling policy.
   */
  public RequestTraceSpanDataBuilder(HttpServletRequest request, TraceEnablingPolicy enablingPolicy) {
    this.request = request;
    this.enablingPolicy = enablingPolicy;
  }

  /**
   * Creates a trace span data builder for the given servlet request and an
   * always-enabled enabling policy.
   */
  public RequestTraceSpanDataBuilder(HttpServletRequest request) {
    this(request, new AlwaysTraceEnablingPolicy());
  }

  @Override
  public TraceContext getTraceContext() {
    boolean enabled = enablingPolicy.isTracingEnabled(getTraceEnabledHeader());
    return new TraceContext(getTraceId(), new SpanIdGenerator().generate(),
        enabled ? TraceContext.TRACE_ENABLED : 0);
  }

  @Override
  public String getName() {
    // TODO: The name should have some configurability since we can't assume
    // the semantics of the application's URL schemes.
    return getFullURL();
  }

  @Override
  public BigInteger getParentSpanId() {
    String parentSpanId = request.getHeader(TraceHeaders.TRACE_SPAN_ID_HEADER);
    if (parentSpanId != null) {
      try {
        return new BigInteger(parentSpanId);
      } catch (NumberFormatException nfe) {
        logger.log(
            Level.WARNING, "Found non-numeric span id in the headers (" + parentSpanId + ")");
      }
    }
    return BigInteger.ZERO;
  }

  /**
   * Looks for a trace id header already on the request and generates a new one if it can't find
   * anything.
   */
  private String getTraceId() {
    String traceId = request.getHeader(TraceHeaders.TRACE_ID_HEADER);
    if (traceId == null) {
      // Let's create one.
      traceId = traceIdGenerator.generate();
    }
    return traceId;
  }

  /**
   * Looks for the enabled flag value on the request headers.
   */
  private boolean getTraceEnabledHeader() {
    String enabledHeader = request.getHeader(TraceHeaders.TRACE_ENABLED_HEADER);
    if (enabledHeader != null) {
      try {
        return Boolean.parseBoolean(enabledHeader);
      } catch (NumberFormatException nfe) {
        logger.log(
            Level.WARNING, "Found non-boolean enabled flag in the headers (" + enabledHeader + ")");
      }
    }
    return false;
  }
  

  /**
   * Helper method to get the servlet request URL in the format we use for trace id's started by
   * requests.
   */
  private String getFullURL() {
    String requestURL = request.getRequestURI();
    String queryString = request.getQueryString();

    if (queryString == null) {
      return requestURL.toString();
    } else {
      return requestURL + '?' + queryString;
    }
  }
}

