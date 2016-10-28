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

package com.google.cloud.trace.v1.producer;

import com.google.cloud.trace.core.Label;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.SpanKind;
import com.google.cloud.trace.core.StackFrame;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.Timestamp;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.core.TraceId;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.TraceSpan;

/**
 * A class that contains helper methods to convert trace events to Stackdriver Trace API v1 trace
 * messages. Each method creates a new trace message with a single span that represents the trace
 * event.
 *
 * @see Labels
 * @see SpanKind
 * @see StackTrace
 * @see Timestamp
 * @see Trace
 * @see TraceContext
 */
public class TraceProducer {
  /**
   * Converts a start span event into an API v1 trace message.
   *
   * @param projectId     a string that contains the Google Cloud Platform project identifier.
   * @param context       a trace context that represents the event span.
   * @param parentContext a trace context that represents the parent span of the event span.
   * @param spanKind      the span kind of the event span.
   * @param name          a string containing the name of the event span.
   * @param timestamp     a timestamp that represents the start time of the event span.
   * @return a trace message that represents the start span event.
   */
  public Trace generateStartSpan(String projectId, TraceContext context,
      TraceContext parentContext, SpanKind spanKind, String name, Timestamp timestamp) {
    TraceSpan.Builder spanBuilder =
        TraceSpan.newBuilder()
            .setSpanId(context.getSpanId().getSpanId())
            .setKind(toSpanKindProto(spanKind))
            .setName(name)
            .setStartTime(toTimestamp(timestamp));

    if (parentContext.getTraceId().equals(context.getTraceId())
        && parentContext.getSpanId().isValid()) {
      spanBuilder.setParentSpanId(parentContext.getSpanId().getSpanId());
    }

    Trace.Builder traceBuilder =
        Trace.newBuilder()
            .setProjectId(projectId)
            .setTraceId(formatTraceId(context.getTraceId()))
            .addSpans(spanBuilder.build());

    return traceBuilder.build();
  }

  /**
   * Converts an end span event into an API v1 trace message.
   *
   * @param projectId a string that contains the Google Cloud Platform project identifier.
   * @param context   a trace context that represents the event span.
   * @param timestamp a timestamp that represents the end time of the event span.
   * @return a trace message that represents the end span event.
   */
  public Trace generateEndSpan(String projectId, TraceContext context, Timestamp timestamp) {
    TraceSpan.Builder spanBuilder =
        TraceSpan.newBuilder()
            .setSpanId(context.getSpanId().getSpanId())
            .setEndTime(toTimestamp(timestamp));

    Trace.Builder traceBuilder =
        Trace.newBuilder()
            .setProjectId(projectId)
            .setTraceId(formatTraceId(context.getTraceId()))
            .addSpans(spanBuilder.build());

    return traceBuilder.build();
  }

  /**
   * Converts a span label annotation event into an API v1 trace message.
   *
   * @param projectId a string that contains the Google Cloud Platform project identifier.
   * @param context   a trace context that represents the event span.
   * @param labels    a labels containing the label annotations for the event span.
   * @return a trace message that represents the span label annotation event.
   */
  public Trace generateAnnotateSpan(String projectId, TraceContext context, Labels labels) {
    TraceSpan.Builder spanBuilder =
        TraceSpan.newBuilder().setSpanId(context.getSpanId().getSpanId());

    for (Label label : labels.getLabels()) {
      spanBuilder.putLabels(label.getKey(), label.getValue());
    }

    Trace.Builder traceBuilder =
        Trace.newBuilder()
            .setProjectId(projectId)
            .setTraceId(formatTraceId(context.getTraceId()))
            .addSpans(spanBuilder.build());

    return traceBuilder.build();
  }

  /**
   * Converts a stack trace annotation event into an API v1 trace message.
   *
   * @param projectId  a string that contains the Google Cloud Platform project identifier.
   * @param context    a trace context that represents the event span.
   * @param stackTrace a stack trace containing the stack trace annotations for the event span.
   * @return a trace message that represents the stack trace annotation event.
   */
  public Trace generateSetStackTrace(
      String projectId, TraceContext context, StackTrace stackTrace) {

    TraceSpan.Builder spanBuilder =
        TraceSpan.newBuilder().setSpanId(context.getSpanId().getSpanId());

    StringBuffer stackTraceValue = new StringBuffer("{\"stack_frame\":[");
    for (int i = 0; i < stackTrace.getStackFrames().size(); i++) {
      if (i != 0) {
        stackTraceValue.append(",");
      }
      StackFrame stackFrame = stackTrace.getStackFrames().get(i);
      stackTraceValue.append("{\"class_name\":\"");
      stackTraceValue.append(stackFrame.getClassName());
      stackTraceValue.append("\",\"method_name\":\"");
      stackTraceValue.append(stackFrame.getMethodName());
      stackTraceValue.append("\"");
      if (stackFrame.getFileName() != null) {
        stackTraceValue.append(",\"file_name\":\"");
        stackTraceValue.append(stackFrame.getFileName());
        stackTraceValue.append("\"");
      }
      if (stackFrame.getLineNumber() != null) {
        stackTraceValue.append(",\"line_number\":");
        stackTraceValue.append(stackFrame.getLineNumber());
      }
      stackTraceValue.append("}");
    }
    stackTraceValue.append("]}");

    spanBuilder.putLabels(
        "trace.cloud.google.com/stacktrace", stackTraceValue.toString());

    Trace.Builder traceBuilder =
        Trace.newBuilder()
            .setProjectId(projectId)
            .setTraceId(formatTraceId(context.getTraceId()))
            .addSpans(spanBuilder.build());

    return traceBuilder.build();
  }

  private String formatTraceId(TraceId traceId) {
    return String.format("%032x", traceId.getTraceId());
  }

  private TraceSpan.SpanKind toSpanKindProto(SpanKind spanKind) {
    switch(spanKind) {
      case UNSPECIFIED:
        return TraceSpan.SpanKind.SPAN_KIND_UNSPECIFIED;
      case RPC_SERVER:
        return TraceSpan.SpanKind.RPC_SERVER;
      case RPC_CLIENT:
        return TraceSpan.SpanKind.RPC_CLIENT;
      default:
        return TraceSpan.SpanKind.SPAN_KIND_UNSPECIFIED;
    }
  }

  private com.google.protobuf.Timestamp toTimestamp(Timestamp timestamp) {
    return com.google.protobuf.Timestamp.newBuilder()
      .setSeconds(timestamp.getSeconds())
      .setNanos(timestamp.getNanos())
      .build();
  }
}
