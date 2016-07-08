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
