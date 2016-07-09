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
 * An interface used for the ingestion and transmission of trace information. This is a tracer that
 * is meant to be used to instrument application code. It contains methods for starting and stopping
 * spans and adding label and stack trace annotations to spans. It operates on a stack of trace
 * contexts, where new spans are created as children of the span represented by the trace context on
 * the top of the stack, and the span represented by the trace context on the top of the stack can
 * be ended. The new trace context and the ending trace context are pushed on and popped off the
 * stack, respectively.
 *
 * @see EndSpanOptions
 * @see Labels
 * @see StackTrace
 * @see StartSpanOptions
 * @see TraceContext
 * @see Tracer
 */
public interface ManagedTracer {
  /**
   * Starts a new span. The new span's parent will be the span identified by the trace context on
   * the top of the stack.
   *
   * @param name a string that represents the name of the new span.
   */
  void startSpan(String name);

  /**
   * Starts a new span. The new span's parent will be the span identified by the trace context on
   * the top of the stack
   *
   * @param name    a string that represents the name of the new span.
   * @param options a start span options that contains overrides for default span values.
   */
  void startSpan(String name, StartSpanOptions options);

  /**
   * Ends the span represented by the trace context on top of the stack.
   */
  void endSpan();

  /**
   * Ends the span represented by the trace context on top of the stack.
   *
   * @param options an end span options that contains overrides for default span values.
   */
  void endSpan(EndSpanOptions options);

  /**
   * Adds label annotations to the span represented by the trace context on top of the stack.
   *
   * @param labels  a labels containing label annotations to add to the span.
   */
  void annotateSpan(Labels labels);

  /**
   * Adds a stack trace label annotation to the span represented by the trace context on top of the
   * stack.
   *
   * @param stackTrace a stack trace to add to the span as a label annotation.
   */
  void setStackTrace(StackTrace stackTrace);

  /**
   * Returns the trace context on top of the stack.
   *
   * @return the trace context on top of the stack.
   */
  TraceContext getCurrentTraceContext();
}
