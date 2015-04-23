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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Tracks the key information about a trace span, including timings and
 * data attributes.
 */
public class TraceSpanData {

  private final TraceContext context;
  private final BigInteger parentSpanId;
  private String name;
  private final long startTimeMillis;
  private long endTimeMillis;
  private Map<String, TraceSpanLabel> labelMap = new HashMap<>();
  private boolean shouldWrite;
  
  private static final SpanIdGenerator spanIdGenerator = new SpanIdGenerator();

  // Package-scoped for testability.
  static Clock clock = new SystemClock();
  
  /**
   * Opens up a new trace span in the given project and assigns it a span id.
   */
  public TraceSpanData(String traceId, String name,
      BigInteger parentSpanId, boolean shouldWrite) {
    this.context = new TraceContext(traceId, spanIdGenerator.generate());
    ThreadTraceContext.push(this.context);
    this.name = name;
    this.parentSpanId = parentSpanId;
    this.startTimeMillis = clock.currentTimeMillis();
    this.shouldWrite = shouldWrite;
  }

  /**
   * Ends the trace span by capturing an end time.
   */
  public void close() {
    if (this.endTimeMillis == 0) {
      // Not closed yet.
      ThreadTraceContext.pop();
      this.endTimeMillis = clock.currentTimeMillis();      
    }
  }

  /**
   * Creates a new child span data object with the given name.
   */
  public TraceSpanData createChildSpanData(String name) {
    return new TraceSpanData(context.getTraceId(), name, context.getSpanId(),
        shouldWrite);
  }

  @Override
  public String toString() {
    StringBuilder labelStr = new StringBuilder();
    for (Map.Entry<String, TraceSpanLabel> labelEntry : labelMap.entrySet()) {
      labelStr.append(labelEntry.getValue());
      labelStr.append('|');
    }
    return context.getTraceId() + '|' + name + '|' + parentSpanId + '|'
        + context.getSpanId() + '|' + startTimeMillis + '|' + endTimeMillis + '|' + labelStr;
  }

  public void addLabel(TraceSpanLabel label) {
    labelMap.put(label.getKey(), label);
  }

  public void addLabel(String key, String value) {
    TraceSpanLabel label = new TraceSpanLabel(key, value);
    labelMap.put(label.getKey(), label);
  }

  public String getTraceId() {
    return context.getTraceId();
  }

  public void setEndTimeMillis(long endTimeMillis) {
    this.endTimeMillis = endTimeMillis;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigInteger getParentSpanId() {
    return parentSpanId;
  }

  public BigInteger getSpanId() {
    return context.getSpanId();
  }

  public long getStartTimeMillis() {
    return startTimeMillis;
  }

  public long getEndTimeMillis() {
    return endTimeMillis;
  }

  public Map<String, TraceSpanLabel> getLabelMap() {
    return labelMap;
  }
  
  public TraceContext getContext() {
    return context;
  }

  public boolean getShouldWrite() {
    return shouldWrite;
  }

  public void setShouldWrite(boolean shouldWrite) {
    this.shouldWrite = shouldWrite;
  }
}
