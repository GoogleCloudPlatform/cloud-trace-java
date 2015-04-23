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

import com.google.cloud.trace.sdk.TraceHeaders;
import com.google.cloud.trace.sdk.TraceSpanData;
import com.google.cloud.trace.sdk.TraceSpanLabel;

import javax.servlet.http.HttpServletResponse;

/**
 * Utilities for working with {@link TraceSpanData}s in the context of servlet responses.
 */
public class TraceResponseUtils {

  /**
   * Sets the given context into the given servlet response.
   */
  public void closeResponseSpanData(TraceSpanData spanData, HttpServletResponse response) {
    // Set the trace context on the response.
    response.setHeader(TraceHeaders.TRACE_ID_HEADER, spanData.getContext().getTraceId());
    response.setHeader(TraceHeaders.TRACE_SPAN_ID_HEADER, "" + spanData.getContext().getSpanId());

    // Fill in a standard label on the span.
    TraceSpanLabel httpResponseLabel =
        new TraceSpanLabel(HttpServletSpanLabels.HTTP_RESPONSE_CODE_LABEL_KEY,
            "" + response.getStatus());
    spanData.addLabel(httpResponseLabel);
  }
}
