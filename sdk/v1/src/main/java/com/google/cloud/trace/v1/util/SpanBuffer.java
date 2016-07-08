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

import com.google.devtools.cloudtrace.v1.TraceSpan;

import java.util.HashMap;

public class SpanBuffer {
  private final HashMap<Long, TraceSpan.Builder> spanMap;

  public SpanBuffer() {
    this.spanMap = new HashMap<Long, TraceSpan.Builder>();
  }

  public void put(TraceSpan span) {
    long spanId = span.getSpanId();
    TraceSpan.Builder builder = spanMap.get(spanId);
    if (builder == null) {
      spanMap.put(spanId, span.toBuilder());
    } else {
      spanMap.put(spanId, builder.mergeFrom(span));
    }
  }

  public Iterable<TraceSpan.Builder> getSpans() {
    return spanMap.values();
  }
}
