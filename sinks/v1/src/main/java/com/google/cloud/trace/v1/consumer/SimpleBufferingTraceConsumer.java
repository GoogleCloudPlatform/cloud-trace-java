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

import com.google.cloud.trace.v1.util.TraceBuffer;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.Traces;

/**
 * A trace consumer that buffers trace messages until it is flushed. When flushed, this consumer sends its
 * buffered trace messages to a delegate trace consumer. The operations on this trace consumer are
 * thread-safe.
 *
 * @see FlushableTraceConsumer
 * @see Traces
 * @see TraceConsumer
 */
public class SimpleBufferingTraceConsumer implements FlushableTraceConsumer {
  private final TraceConsumer traceConsumer;
  private TraceBuffer traceBuffer;

  private final Object monitor = new Object();

  /**
   * Creates a trace consumer that buffers trace messages until it is flushed.
   *
   * @param traceConsumer a trace consumer that serves as the delegate of this trace consumer.
   */
  public SimpleBufferingTraceConsumer(TraceConsumer traceConsumer) {
    this.traceConsumer = traceConsumer;
    this.traceBuffer = new TraceBuffer();
  }

  @Override
  public void receive(Traces traces) {
    synchronized(monitor) {
      for (Trace trace : traces.getTracesList()) {
        traceBuffer.put(trace);
      }
    }
  }

  @Override
  public void flush() {
    TraceBuffer previous;
    synchronized(monitor) {
      previous = traceBuffer;
      traceBuffer = new TraceBuffer();
    }
    if (!previous.isEmpty()) {
      Traces traces = previous.getTraces();
      traceConsumer.receive(traces);
    }
  }
}
