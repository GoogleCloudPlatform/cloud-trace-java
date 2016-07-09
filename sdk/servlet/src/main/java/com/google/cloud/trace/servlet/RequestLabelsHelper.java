// Copyright 2016 Google Inc. All rights reserved.
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

package com.google.cloud.trace.servlet;

import com.google.cloud.trace.util.Labels;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utilities for generating span data related to HTTP servlets.
 *
 * @see HttpServletRequest
 * @see HttpServletResponse
 * @see Labels
 * @see Labels#Builder
 */
public class RequestLabelsHelper {
  /**
   * Returns a span name generated from the given HTTP servlet request.
   *
   * @param request the http servlet request used to generate the span name.
   * @return a string that contains the span name.
   */
  public static String overrideName(HttpServletRequest request) {
    String name = request.getContextPath() + request.getServletPath() + request.getPathInfo();
    return name;
  }

  /**
   * Adds span label annotations based on the given HTTP servlet request to the given labels
   * builder.
   *
   * @param request       the http servlet request used to generate the span label annotations.
   * @param labelsBuilder the labels builder to add span label annotations to.
   */
  public static void addRequestLabels(HttpServletRequest request, Labels.Builder labelsBuilder) {
    labelsBuilder.add("trace.cloud.google.com/http/method", request.getMethod());
    labelsBuilder.add("trace.cloud.google.com/http/url", request.getRequestURL().toString());
    if (request.getContentLength() != -1) {
      labelsBuilder.add(
          "trace.cloud.google.com/http/request/size", Integer.toString(request.getContentLength()));
    }
    labelsBuilder.add("trace.cloud.google.com/http/host", request.getServerName());
    if (request.getHeader("user-agent") != null) {
      labelsBuilder.add("trace.cloud.google.com/http/user_agent", request.getHeader("user-agent"));
    }
  }

  /**
   * Adds span label annotations based on the given HTTP servlet response to the given labels
   * builder.
   *
   * @param response      the http servlet response used to generate the span label annotations.
   * @param labelsBuilder the labels builder to add span label annotations to.
   */
  public static void addResponseLabels(HttpServletResponse response, Labels.Builder labelsBuilder) {
    // Add "trace.cloud.google.com/http/status_code" to Integer.toString(response.getStatus()), if
    // GAE supports 3.0.
    if (response.getBufferSize() > 0) {
      labelsBuilder.add(
          "trace.cloud.google.com/http/response/size", Integer.toString(response.getBufferSize()));
    }
  }
}
