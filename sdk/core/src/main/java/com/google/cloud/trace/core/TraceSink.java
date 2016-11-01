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

package com.google.cloud.trace.core;

import com.google.cloud.trace.ManagedTracer;
import com.google.cloud.trace.Tracer;

/**
 * A tracer that is used to receive trace data. This tracer is not designed to be used to implement
 * application code.
 *
 * @see Labels
 * @see ManagedTracer
 * @see SpanKind
 * @see StackTrace
 * @see Timestamp
 * @see SpanContext
 * @see Tracer
 */
public interface TraceSink {
  /**
   * Starts a new span.
   *
   * @param context       the span context of the new span.
   * @param parentContext the span context of the parent span, if valid.
   * @param spanKind      the span kind of the new span.
   * @param name          a string that represents the name of the new span.
   * @param timestamp     the timestamp for the start of the new span.
   */
  void startSpan(SpanContext context, SpanContext parentContext, SpanKind spanKind,
      String name, Timestamp timestamp);

  /**
   * Ends a span.
   *
   * @param context   the span context of the span to end.
   * @param timestamp the timestamp for the end of the span to end.
   */
  void endSpan(SpanContext context, Timestamp timestamp);

  /**
   * Adds label annotations to a span.
   *
   * @param context the span context of the span to annotate.
   * @param labels  a labels containing label annotations to add to the span.
   */
  void annotateSpan(SpanContext context, Labels labels);

  /**
   * Adds a stack trace label annotation to a span.
   *
   * @param context    the span context of the span to annotate.
   * @param stackTrace a stack trace to add to the span as a label annotation.
   */
  void setStackTrace(SpanContext context, StackTrace stackTrace);
}
