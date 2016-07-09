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

package com.google.cloud.trace.v1.util;

import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.TraceSpan;
import java.util.Map;

/**
 * A class for sizing traces.
 *
 * @see Sizer
 * @see Trace
 */
public class RoughTraceSizer implements Sizer<Trace> {
  /**
   * Sizes a trace.
   *
   * @param trace a trace to be sized.
   * @return the size of the trace.
   */
  @Override
  public int size(Trace trace) {
    int size = 0;
    size += trace.getProjectId().length();
    size += trace.getTraceId().length();
    for (TraceSpan span : trace.getSpansList()) {
      size += spanSize(span);
    }
    return size;
  }

  private int spanSize(TraceSpan span) {
    int size = 0;
    size += 8; // For span_id.
    size += 4; // For kind.
    size += span.getName().length();
    size += 16; // For start_time.
    size += 16; // For end_time.
    size += 8; // For parent_span_id.
    size += labelsSize(span);
    return size;
  }

  private int labelsSize(TraceSpan span) {
    int size = 0;
    for (Map.Entry<String, String> label : span.getLabels().entrySet()) {
      size += label.getKey().length() + label.getValue().length();
    }
    return size;
  }
}
