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

package com.google.cloud.trace.v1;

import com.google.cloud.trace.RawTracer;
import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.Timestamp;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceContextFactory;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.cloud.trace.v1.source.TraceSource;
import com.google.devtools.cloudtrace.v1.Trace;

public class RawTracerV1 implements RawTracer {
  private final String projectId;
  private final TraceSource traceSource;
  private final TraceSink traceSink;

  public RawTracerV1(String projectId, TraceSource traceSource, TraceSink traceSink) {
    this.projectId = projectId;
    this.traceSource = traceSource;
    this.traceSink = traceSink;
  }

  @Override
  public void startSpan(TraceContext context, TraceContext parentContext,
      SpanKind spanKind, String name, Timestamp timestamp) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceSource.generateStartSpan(
          projectId, context, parentContext, spanKind, name, timestamp);
      traceSink.receive(trace);
    }
  }

  @Override
  public void endSpan(TraceContext context, Timestamp timestamp) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceSource.generateEndSpan(projectId, context, timestamp);
      traceSink.receive(trace);
    }
  }

  @Override
  public void annotateSpan(TraceContext context, Labels labels) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceSource.generateAnnotateSpan(projectId, context, labels);
      traceSink.receive(trace);
    }
  }

  @Override
  public void setStackTrace(TraceContext context, StackTrace stackTrace) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceSource.generateSetStackTrace(projectId, context, stackTrace);
      traceSink.receive(trace);
    }
  }
}
