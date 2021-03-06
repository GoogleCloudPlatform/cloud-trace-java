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
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A flushable trace consumer that auto-flushes when its buffered trace messages exceed its buffer size
 * or have been buffered for longer than its scheduled delay. The operations on this trace consumer are
 * thread-safe.
 *
 * @see FlushableTraceConsumer
 * @see Sizer
 * @see Traces
 * @see TraceConsumer
 */
public class ScheduledBufferingTraceConsumer implements FlushableTraceConsumer {
  private final TraceConsumer traceConsumer;
  private final Sizer<Trace> traceSizer;
  private final int bufferSize;
  private final int scheduledDelay;
  private final ScheduledExecutorService scheduler;

  private TraceBuffer traceBuffer = new TraceBuffer();

  private final Object monitor = new Object();

  private int size = 0;
  private Future<?> autoFlusher = null;
  private ScheduledFuture<?> flusher = null;

  /**
   * Creates a buffering trace consumer.
   *
   * @param traceConsumer      a trace consumer that serves as this trace consumer's delegate.
   * @param traceSizer     a sizer used to estimate the size of trace messages.
   * @param bufferSize     the size of this trace consumer's trace message buffer.
   * @param scheduledDelay the scheduled delay of this trace consumer in seconds.
   * @param scheduler      a scheduled executor service used to automatically flush this trace consumer.
   */
  public ScheduledBufferingTraceConsumer(TraceConsumer traceConsumer, Sizer<Trace> traceSizer, int bufferSize,
      int scheduledDelay, ScheduledExecutorService scheduler) {
    this.traceConsumer = traceConsumer;
    this.traceSizer = traceSizer;
    this.bufferSize = bufferSize;
    this.scheduledDelay = scheduledDelay;
    this.scheduler = scheduler;
  }

  @Override
  public void receive(Traces traces) {
    synchronized(monitor) {
      for (Trace trace : traces.getTracesList()) {
        traceBuffer.put(trace);
        size += traceSizer.size(trace);
        if (size >= bufferSize) {
          if (autoFlusher == null) {
            autoFlusher = scheduler.submit(flushable());
          }
        } else {
          if ((flusher == null) && (autoFlusher == null)) {
            flusher = scheduler.schedule(flushable(), scheduledDelay, TimeUnit.SECONDS);
          }
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
      if (autoFlusher != null) {
        autoFlusher.cancel(false);
        autoFlusher = null;
      }
      if (flusher != null) {
        flusher.cancel(false);
        flusher = null;
      }
    }
    if (!previous.isEmpty()) {
      Traces traces = previous.getTraces();
      traceConsumer.receive(traces);
    }
  }

  private Runnable flushable() {
    return new Runnable() {
      @Override
      public void run() {
        flush();
      }
    };
  }
}
