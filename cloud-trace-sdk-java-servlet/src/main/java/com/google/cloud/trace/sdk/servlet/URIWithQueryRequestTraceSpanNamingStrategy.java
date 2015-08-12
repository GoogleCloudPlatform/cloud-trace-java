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

import com.google.cloud.trace.sdk.TraceSpanData;

import javax.servlet.http.HttpServletRequest;

/**
 * Names a new {@link TraceSpanData}s after the URI component of the request plus
 * the query string (if any).
 * Leaves off the query string and (per getRequestURI) the server name/port/protocol.
 */
public class URIWithQueryRequestTraceSpanNamingStrategy implements RequestTraceSpanNamingStrategy {
  /**
   * Generates a name for a new {@link TraceSpanData} from the given request.
   */
  @Override
  public String getName(HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    String queryString = request.getQueryString();

    if (queryString == null) {
      return requestURI.toString();
    } else {
      return requestURI + '?' + queryString;
    }
  }
}
