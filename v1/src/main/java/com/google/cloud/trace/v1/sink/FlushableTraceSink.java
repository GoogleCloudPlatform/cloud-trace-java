package com.google.cloud.trace.v1.sink;

public interface FlushableTraceSink extends TraceSink {
  void flush();
}
