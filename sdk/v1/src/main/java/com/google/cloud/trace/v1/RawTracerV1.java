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
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.cloud.trace.v1.source.TraceSource;
import com.google.devtools.cloudtrace.v1.Trace;

/**
 * A raw tracer that converts trace events to Stackdriver Trace API v1 trace messages and dispatches
 * them to a trace sink.
 *
 * <p>Each method examines the trace options on the given trace context. If the trace enabled option
 * is set, the corresponding method on the trace source is called, and the resulting trace message
 * is sent to the trace sink. If the trace enabled option is not set, the trace event is ignored.
 *
 * <p>Stackdriver Trace API v1 trace messages contain the project identifier of the Google Cloud
 * Platform project that owns the trace information. The project identifier is either the Project ID
 * string for the project as seen in the Google Cloud Console or a decimal string representation of
 * the Project Number of the project.
 *
 * @see <a href="https://cloud.google.com/trace/api">Stackdriver Trace API</a>
 * @see RawTracer
 * @see Trace
 * @see TraceSink
 * @see TraceSource
 */
public class RawTracerV1 implements RawTracer {
  private final String projectId;
  private final TraceSource traceSource;
  private final TraceSink traceSink;

  /**
   * Creates a raw tracer.
   *
   * @param projectId   a string containing the project identifier of the Google Cloud Platform
   *                    project that owns the trace information.
   * @param traceSource a trace source that converts trace events to API v1 trace messages.
   * @param traceSink   a trace sink that accepts API v1 trace messages.
   */
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
