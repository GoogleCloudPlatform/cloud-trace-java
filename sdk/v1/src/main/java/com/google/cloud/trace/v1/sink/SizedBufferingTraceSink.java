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

package com.google.cloud.trace.v1.sink;

import com.google.cloud.trace.v1.util.Sizer;
import com.google.cloud.trace.v1.util.TraceBuffer;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.Traces;

/**
 * A flushable trace sink that auto-flushes when its buffered trace messages exceed its buffer size.
 * This sink uses a trace sizer to estimate the size of its buffered trace messages. The operations
 * on this trace sink are thread-safe.
 *
 * @see FlushableTraceSink
 * @see Sizer
 * @see Trace
 * @see TraceSink
 */
public class SizedBufferingTraceSink implements FlushableTraceSink {
  private final TraceSink traceSink;
  private final Sizer<Trace> traceSizer;
  private final int bufferSize;
  private final TraceBuffer traceBuffer;

  private int size;

  private final Object monitor = new Object();

  /**
   * Creates a buffering trace sink.
   *
   * @param traceSink  a trace sink that serves as this trace sink's delegate.
   * @param traceSizer a sizer used to estimate the size of trace messages.
   * @param bufferSize the size of this trace sink's trace message buffer.
   */
  public SizedBufferingTraceSink(TraceSink traceSink, Sizer<Trace> traceSizer, int bufferSize) {
    this.traceSink = traceSink;
    this.traceSizer = traceSizer;
    this.bufferSize = bufferSize;
    this.traceBuffer = new TraceBuffer();
    this.size = 0;
  }

  @Override
  public void receive(Trace trace) {
    synchronized(monitor) {
      size += traceSizer.size(trace);
      traceBuffer.put(trace);
      if (size >= bufferSize) {
        flush();
      }
    }
  }

  @Override
  public void flush() {
    Traces traces;
    synchronized(monitor) {
      traces = traceBuffer.getTraces();
      traceBuffer.clear();
      size = 0;
    }
    for (Trace trace : traces.getTracesList()) {
      traceSink.receive(trace);
    }
  }
}
