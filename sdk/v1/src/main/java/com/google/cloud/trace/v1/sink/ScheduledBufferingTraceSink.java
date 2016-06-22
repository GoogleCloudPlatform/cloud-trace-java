package com.google.cloud.trace.v1.sink;

import com.google.cloud.trace.v1.util.Sizer;
import com.google.cloud.trace.v1.util.TraceBuffer;
import com.google.common.collect.ImmutableList;
import com.google.devtools.cloudtrace.v1.Trace;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledBufferingTraceSink implements FlushableTraceSink {
  private final TraceSink traceSink;
  private final Sizer<Trace> traceSizer;
  private final int bufferSize;
  private final int scheduledDelay;
  private final ScheduledExecutorService scheduler;

  private final TraceBuffer traceBuffer = new TraceBuffer();

  private final Object monitor = new Object();

  private int size = 0;
  private Future<?> autoFlusher = null;
  private ScheduledFuture<?> flusher = null;

  public ScheduledBufferingTraceSink(TraceSink traceSink, Sizer<Trace> traceSizer, int bufferSize,
      int scheduledDelay, ScheduledExecutorService scheduler) {
    this.traceSink = traceSink;
    this.traceSizer = traceSizer;
    this.bufferSize = bufferSize;
    this.scheduledDelay = scheduledDelay;
    this.scheduler = scheduler;
  }

  @Override
  public void receive(Trace trace) {
    synchronized(monitor) {
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

  @Override
  public void flush() {
    ImmutableList<Trace> traces;
    synchronized(monitor) {
      traces = ImmutableList.copyOf(traceBuffer.getTraces());
      traceBuffer.clear();
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
    for (Trace trace : traces) {
      traceSink.receive(trace);
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
