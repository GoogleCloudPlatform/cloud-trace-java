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

import java.util.List;

/**
 * Writes out {@link TraceSpanData}s.
 */
public interface TraceWriter {
  /**
   * Writes out a single {@link TraceSpanData}. Should close it if it isn't
   * already closed.
   */
  void writeSpan(TraceSpanData span) throws CloudTraceException;
  
  /**
   * Writes out a batch of {@link TraceSpanData}s. Should close them if they
   * aren't already.
   */
  void writeSpans(List<TraceSpanData> spans) throws CloudTraceException;
  
  /**
   * Writes out a batch of {@link TraceSpanData}s. Should close them if they
   * aren't already.
   */
  void writeSpans(TraceSpanData... spans) throws CloudTraceException;
  
  /**
   * Provided as an opportunity for graceful cleanup.
   */
  void shutdown() throws CloudTraceException;
}
