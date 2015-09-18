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

package com.google.cloud.trace.sdk;

import java.math.BigInteger;
import java.util.Map;

/**
 * Exposes the data necessary to create fully-fleshed-out {@link TraceSpanData}s.
 */
public interface TraceSpanDataBuilder {
  TraceContext getTraceContext();

  String getName();

  BigInteger getParentSpanId();

  Map<String, TraceSpanLabel> getLabelMap();
  
  long getStartTimeMillis();
  
  long getEndTimeMillis();
  
  /**
   * Creates a new builder whose resulting trace span(s) will be children
   * of the span(s) built by this one.
   * @param childSpanName The name of the child span.
   * @return the new builder.
   */
  TraceSpanDataBuilder createChild(String childSpanName);
}
