package com.google.cloud.trace.v1.sink;

import com.google.cloud.trace.v1.util.TraceBuffer;
import com.google.devtools.cloudtrace.v1.Trace;

public class SimpleBufferingTraceSink implements FlushableTraceSink {
  private final TraceSink traceSink;
  private final TraceBuffer traceBuffer;

  private final Object monitor = new Object();

  public SimpleBufferingTraceSink(TraceSink traceSink) {
    this.traceSink = traceSink;
    this.traceBuffer = new TraceBuffer();
  }

  @Override
  public void receive(Trace trace) {
    synchronized(monitor) {
      traceBuffer.put(trace);
    }
  }

  @Override
  public void flush() {
    synchronized(monitor) {
      for (Trace trace : traceBuffer.getTraces()) {
        traceSink.receive(trace);
      }
    }
  }
}
