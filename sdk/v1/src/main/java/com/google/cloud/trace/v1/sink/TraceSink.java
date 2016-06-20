package com.google.cloud.trace.v1.sink;

import com.google.devtools.cloudtrace.v1.Trace;

public interface TraceSink {
  void receive(Trace trace);
}
