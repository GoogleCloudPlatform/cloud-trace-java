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

/**
 * Helper class that wraps a {@link TraceSpanData} and automates the process of
 * starting and ending it and writing it out to a given {@link TraceWriter}.
 */
public class TraceSpanDataHandle implements AutoCloseable {

  private final TraceSpanDataBuilder spanDataBuilder;
  private final TraceWriter writer;
  private final TraceSpanData spanData;
  
  public TraceSpanDataHandle(TraceSpanDataBuilder spanDataBuilder, TraceWriter writer) {
    this.spanDataBuilder = spanDataBuilder;
    this.writer = writer;
    spanData = new TraceSpanData(spanDataBuilder);
    spanData.start();
  }

  public TraceSpanData getSpanData() {
    return spanData;
  }
  
  public TraceSpanDataHandle createChild(String childSpanName) {
    TraceSpanDataBuilder childSpanBuilder = spanDataBuilder.createChild(childSpanName);
    return new TraceSpanDataHandle(childSpanBuilder, writer);
  }
  
  @Override
  public void close() throws Exception {
    if (!spanData.isEnded()) {
      spanData.end();
      writer.writeSpan(spanData);
    }
  }
}
