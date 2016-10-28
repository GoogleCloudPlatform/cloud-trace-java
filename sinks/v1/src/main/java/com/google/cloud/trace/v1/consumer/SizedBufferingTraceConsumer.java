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

package com.google.cloud.trace.v1.consumer;

import com.google.cloud.trace.v1.util.Sizer;
import com.google.cloud.trace.v1.util.TraceBuffer;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.Traces;

/**
 * A flushable trace consumer that auto-flushes when its buffered trace messages exceed its buffer size.
 * This consumer uses a trace sizer to estimate the size of its buffered trace messages. The operations
 * on this trace consumer are thread-safe.
 *
 * @see FlushableTraceConsumer
 * @see Sizer
 * @see Traces
 * @see TraceConsumer
 */
public class SizedBufferingTraceConsumer implements FlushableTraceConsumer {
  private final TraceConsumer traceConsumer;
  private final Sizer<Trace> traceSizer;
  private final int bufferSize;
  private TraceBuffer traceBuffer;

  private int size;

  private final Object monitor = new Object();

  /**
   * Creates a buffering trace consumer.
   *
   * @param traceConsumer  a trace consumer that serves as this trace consumer's delegate.
   * @param traceSizer a sizer used to estimate the size of trace messages.
   * @param bufferSize the size of this trace consumer's trace message buffer.
   */
  public SizedBufferingTraceConsumer(TraceConsumer traceConsumer, Sizer<Trace> traceSizer, int bufferSize) {
    this.traceConsumer = traceConsumer;
    this.traceSizer = traceSizer;
    this.bufferSize = bufferSize;
    this.traceBuffer = new TraceBuffer();
    this.size = 0;
  }

  @Override
  public void receive(Traces traces) {
    synchronized(monitor) {
      for (Trace trace : traces.getTracesList()) {
        size += traceSizer.size(trace);
        traceBuffer.put(trace);
        if (size >= bufferSize) {
          flush();
        }
      }
    }
  }

  @Override
  public void flush() {
    TraceBuffer previous;
    synchronized(monitor) {
      previous = traceBuffer;
      traceBuffer = new TraceBuffer();
      size = 0;
    }
    if (!previous.isEmpty()) {
      Traces traces = previous.getTraces();
      traceConsumer.receive(traces);
    }
  }
}
