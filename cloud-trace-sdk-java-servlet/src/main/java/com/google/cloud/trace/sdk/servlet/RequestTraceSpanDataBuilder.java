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
import com.google.cloud.trace.sdk.TraceSpanLabel;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Builds trace span data based on an http servlet request.
 */
public class RequestTraceSpanDataBuilder extends AbstractTraceSpanDataBuilder {
  private final HttpServletRequest request;
  private final TraceEnablingPolicy enablingPolicy;
  private final RequestTraceSpanNamingStrategy spanNamingStrategy;
  private final TraceContext incomingContext;
  
  /**
   * Creates a trace span data builder for the given servlet request with the
   * given enabling policy and given span naming strategy.
   */
  public RequestTraceSpanDataBuilder(HttpServletRequest request, TraceEnablingPolicy enablingPolicy,
      RequestTraceSpanNamingStrategy spanNamingStrategy) {
    if (spanNamingStrategy == null) {
      throw new IllegalArgumentException("Span naming strategy must be provided");
    }
    this.request = request;
    this.enablingPolicy = enablingPolicy;
    this.spanNamingStrategy = spanNamingStrategy;
    
    String traceHeaderValue = request.getHeader(TraceContext.TRACE_HEADER);
    if (traceHeaderValue != null) {
      this.incomingContext = TraceContext.fromTraceHeader(traceHeaderValue);
    } else {
      this.incomingContext = null;
    }
  }

  /**
   * Creates a trace span data builder for the given servlet request with the
   * given enabling policy and default span naming strategy.
   */
  public RequestTraceSpanDataBuilder(
      HttpServletRequest request, TraceEnablingPolicy enablingPolicy) {
    this(request, enablingPolicy, new URIWithQueryRequestTraceSpanNamingStrategy());
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
    return spanNamingStrategy.getName(request);
  }

  @Override
  public BigInteger getParentSpanId() {
    if (incomingContext != null && incomingContext.getSpanId() != null) {
      return incomingContext.getSpanId();
    }
    return BigInteger.ZERO;
  }

  @Override
  public Map<String, TraceSpanLabel> getLabelMap() {
    Map<String, TraceSpanLabel> labelMap = new HashMap<>();

    // Start filling in standard labels.
    TraceSpanLabel httpMethodLabel =
        new TraceSpanLabel(HttpServletSpanLabels.HTTP_METHOD_LABEL_KEY, request.getMethod());
    labelMap.put(httpMethodLabel.getKey(), httpMethodLabel);

    TraceSpanLabel httpUrlLabel = new TraceSpanLabel(
        HttpServletSpanLabels.HTTP_URL_LABEL_KEY, getFullUrl());
    labelMap.put(httpUrlLabel.getKey(), httpUrlLabel);

    TraceSpanLabel httpHostLabel =
        new TraceSpanLabel(HttpServletSpanLabels.HTTP_HOST_LABEL_KEY, request.getServerName());
    labelMap.put(httpHostLabel.getKey(), httpHostLabel);

    return labelMap;
  }

  /**
   * Looks for a trace id header already on the request and generates a new one if it can't find
   * anything.
   */
  private String getTraceId() {
    if (incomingContext != null && incomingContext.getTraceId() != null) {
      return incomingContext.getTraceId();
    }
    // Else et's create one.
    return traceIdGenerator.generate();
  }

  /**
   * Looks for the enabled flag value on the request headers.
   */
  private boolean getTraceEnabledHeader() {
    if (incomingContext != null) {
      return incomingContext.getShouldWrite();
    }
    return false;
  }
  
  private String getFullUrl() {
    StringBuffer requestURI = request.getRequestURL();
    String queryString = request.getQueryString();

    if (queryString == null) {
      return requestURI.toString();
    } else {
      return requestURI.append('?').append(queryString).toString();
    }
  }
}
