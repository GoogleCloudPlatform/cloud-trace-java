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

import com.google.cloud.trace.core.TraceSink;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.SpanKind;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.Timestamp;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.v1.consumer.TraceConsumer;
import com.google.cloud.trace.v1.producer.TraceProducer;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.Traces;

/**
 * A raw tracer that converts trace events to Stackdriver Trace API v1 trace messages and dispatches
 * them to a trace consumer.
 *
 * <p>Each method examines the trace options on the given trace context. If the trace enabled option
 * is set, the corresponding method on the trace producer is called, and the resulting trace message
 * is sent to the trace consumer. If the trace enabled option is not set, the trace event is ignored.
 *
 * <p>Stackdriver Trace API v1 trace messages contain the project identifier of the Google Cloud
 * Platform project that owns the trace information. The project identifier is either the Project ID
 * string for the project as seen in the Google Cloud Console or a decimal string representation of
 * the Project Number of the project.
 *
 * @see <a href="https://cloud.google.com/trace/api">Stackdriver Trace API</a>
 * @see TraceSink
 * @see Trace
 * @see TraceConsumer
 * @see TraceProducer
 */
public class TraceSinkV1 implements TraceSink {
  private final String projectId;
  private final TraceProducer traceProducer;
  private final TraceConsumer traceConsumer;

  /**
   * Creates a raw tracer.
   *
   * @param projectId   a string containing the project identifier of the Google Cloud Platform
   *                    project that owns the trace information.
   * @param traceProducer a trace producer that converts trace events to API v1 trace messages.
   * @param traceConsumer   a trace consumer that accepts API v1 trace messages.
   */
  public TraceSinkV1(String projectId, TraceProducer traceProducer, TraceConsumer traceConsumer) {
    this.projectId = projectId;
    this.traceProducer = traceProducer;
    this.traceConsumer = traceConsumer;
  }

  @Override
  public void startSpan(TraceContext context, TraceContext parentContext,
      SpanKind spanKind, String name, Timestamp timestamp) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceProducer.generateStartSpan(
          projectId, context, parentContext, spanKind, name, timestamp);
      traceConsumer.receive(Traces.newBuilder().addTraces(trace).build());
    }
  }

  @Override
  public void endSpan(TraceContext context, Timestamp timestamp) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceProducer.generateEndSpan(projectId, context, timestamp);
      traceConsumer.receive(Traces.newBuilder().addTraces(trace).build());
    }
  }

  @Override
  public void annotateSpan(TraceContext context, Labels labels) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceProducer.generateAnnotateSpan(projectId, context, labels);
      traceConsumer.receive(Traces.newBuilder().addTraces(trace).build());
    }
  }

  @Override
  public void setStackTrace(TraceContext context, StackTrace stackTrace) {
    if (context.getTraceOptions().getTraceEnabled()) {
      Trace trace = traceProducer.generateSetStackTrace(projectId, context, stackTrace);
      traceConsumer.receive(Traces.newBuilder().addTraces(trace).build());
    }
  }
}
