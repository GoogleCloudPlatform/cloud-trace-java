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

package com.google.cloud.trace.sdk;

/**
 * Defines custom HTTP headers for forwarding trace data.
 */
public interface TraceHeaders {
  /**
   * The custom header name for trace id's.
   */
  String TRACE_ID_HEADER = "X-Cloud-Trace-Id";
  
  /**
   * The custom header name for trace spans.
   */
  String TRACE_SPAN_ID_HEADER = "X-Cloud-Trace-Span-Id";
  
  /**
   * The custom header name for the trace-enabled flag.
   */
  String TRACE_ENABLED_HEADER = "X-Cloud-Trace-Enabled";
}
