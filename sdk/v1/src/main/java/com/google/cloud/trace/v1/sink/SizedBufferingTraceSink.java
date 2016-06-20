package com.google.cloud.trace.v1.sink;

import com.google.cloud.trace.v1.util.Sizer;
import com.google.cloud.trace.v1.util.TraceBuffer;
import com.google.devtools.cloudtrace.v1.Trace;

public class SizedBufferingTraceSink implements FlushableTraceSink {
  private final TraceSink traceSink;
  private final Sizer<Trace> traceSizer;
  private final int bufferSize;
  private final TraceBuffer traceBuffer;

  private int size;

  private final Object monitor = new Object();

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
    synchronized(monitor) {
      for (Trace trace : traceBuffer.getTraces()) {
        traceSink.receive(trace);
      }
      traceBuffer.clear();
      size = 0;
    }
  }
}
