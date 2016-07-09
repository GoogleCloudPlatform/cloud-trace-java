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

package com.google.cloud.trace;

import com.google.cloud.trace.util.EndSpanOptions;
import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.StartSpanOptions;
import com.google.cloud.trace.util.TraceContext;

/**
 * An interface used for the ingestion and transmission of trace information. This is a basic tracer
 * that is meant to be used to instrument application code. It contains methods for starting and
 * stopping spans and adding label and stack trace annotations to spans.
 *
 * <p>A trace contains a collection of spans. Each span has a name and also start and end times.
 * Thus traces can be used for application timing and profiling.
 *
 * <p>A trace is identified by a trace id, which is included in the trace. A span is identified by a
 * span id, which is included in the span.
 *
 * <p>Spans can have parents, so they form a hierarchical representation. Thus traces can be used to
 * understand the flow of a given execution of an application. Tracing can be helpful in
 * understanding the relationships between components in a distributed application. If a span has a
 * parent, its parent's span id is included in the span.
 *
 * <p>Spans can also contain labels. These labels are used to annotate spans with additional
 * information. A label is a key-value pair of strings.
 *
 * @see <a href="https://cloud.google.com/trace">Stackdriver Trace</a>
 * @see EndSpanOptions
 * @see Labels
 * @see ManagedTracer
 * @see StackTrace
 * @see StartSpanOptions
 * @see TraceContext
 */
public interface Tracer {
  /**
   * Starts a new span. The new span's parent will be the span identified by {@code parentContext},
   * if valid.
   *
   * @param parentContext the trace context of the parent span, if valid.
   * @param name          a string that represents the name of the new span.
   * @return the trace context of the new span.
   */
  TraceContext startSpan(TraceContext parentContext, String name);

  /**
   * Starts a new span. The new span's parent will be the span identified by {@code parentContext},
   * if valid.
   *
   * @param parentContext the trace context of the parent span, if valid.
   * @param name          a string that represents the name of the new span.
   * @param options       a start span options that contains overrides for default span values.
   * @return the trace context of the new span.
   */
  TraceContext startSpan(TraceContext parentContext, String name, StartSpanOptions options);

  /**
   * Ends a span.
   *
   * @param context the trace context of the span to end.
   */
  void endSpan(TraceContext context);

  /**
   * Ends a span.
   *
   * @param context the trace context of the span to end.
   * @param options an end span options that contains overrides for default span values.
   */
  void endSpan(TraceContext context, EndSpanOptions options);

  /**
   * Adds label annotations to a span.
   *
   * @param context the trace context of the span to annotate.
   * @param labels  a labels containing label annotations to add to the span.
   */
  void annotateSpan(TraceContext context, Labels labels);

  /**
   * Adds a stack trace label annotation to a span.
   *
   * @param context    the trace context of the span to annotate.
   * @param stackTrace a stack trace to add to the span as a label annotation.
   */
  void setStackTrace(TraceContext context, StackTrace stackTrace);
}
