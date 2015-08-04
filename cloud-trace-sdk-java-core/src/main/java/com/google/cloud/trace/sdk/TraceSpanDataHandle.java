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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class that wraps a {@link TraceSpanData} in an {@link AutoCloseable}.
 * Provides a nice programming model for many scenarios where you want to
 * manually trace one or more operations, as it:
 *   a) Automatically starts and ends the span.
 *   b) Automatically leverages the provided writer to write out the span data. 
 */
public class TraceSpanDataHandle implements AutoCloseable {

  private static final Logger logger = Logger.getLogger(TraceSpanDataHandle.class.getName());

  private boolean isClosed = false;
  private final TraceWriter writer;
  private final TraceSpanData span;

  public TraceSpanDataHandle(TraceWriter writer, String traceId, String name,
      BigInteger parentSpanId, boolean shouldWrite) {
    this.writer = writer;
    this.span = new TraceSpanData(traceId, name, parentSpanId, shouldWrite);
    this.span.start();
  }

  /**
   * Creates a child span data object for the wrapped {@link TraceSpanData} and
   * wraps it in a handle of its own.
   */
  public TraceSpanDataHandle createChildSpanDataHandle(String name) {
    if (isClosed) {
      throw new IllegalStateException("TraceSpanData is already closed");
    }
    return new TraceSpanDataHandle(writer, span.getTraceId(), name,
        span.getSpanId(), span.getShouldWrite());
  }

  public TraceSpanData getSpanData() {
    return span;
  }

  @Override
  public void close() {
    span.end();
    isClosed = true;
    try {
      writer.writeSpan(span);
    } catch (CloudTraceException e) {
      logger.log(Level.SEVERE, "Failed to write trace while shutting down", e);
    }
  }
  
  @Override
  public String toString() {
    return span.toString();
  }
}
