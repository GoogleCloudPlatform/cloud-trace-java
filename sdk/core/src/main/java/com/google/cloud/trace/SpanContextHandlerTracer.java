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

import com.google.cloud.trace.core.EndSpanOptions;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.TraceContext;

/**
 * A managed tracer that maintains trace context.
 *
 * <p>This tracer maintains the current trace context and delegates calls to another tracer.</p>
 */
public class SpanContextHandlerTracer implements ManagedTracer  {
  private final Tracer tracer;
  private final SpanContextHandler contextHandler;

  /**
   * Creates a new managed tracer.
   *
   * @param tracer         a tracer that serves as a delegate for all tracer functionality.
   * @param contextHandler a span context handler that manages the current span context.
   */
  public SpanContextHandlerTracer(Tracer tracer, SpanContextHandler contextHandler) {
    this.tracer = tracer;
    this.contextHandler = contextHandler;
  }

  @Override
  public TraceContext startSpan(String name) {
    SpanContext parent = contextHandler.current();
    SpanContext child = tracer.startSpan(parent, name);
    contextHandler.attach(child);
    return new TraceContext(child, parent);
  }

  @Override
  public TraceContext startSpan(String name, StartSpanOptions options) {
    SpanContext parent = contextHandler.current();
    SpanContext child = tracer.startSpan(parent, name, options);
    contextHandler.attach(child);
    return new TraceContext(child, parent);
  }

  @Override
  public void endSpan(TraceContext traceContext) {
    tracer.endSpan(traceContext.getCurrent());
    contextHandler.detach(traceContext.getParent());
  }

  @Override
  public void endSpan(TraceContext traceContext, EndSpanOptions options) {
    tracer.endSpan(traceContext.getCurrent(), options);
    contextHandler.detach(traceContext.getParent());
  }

  @Override
  public void annotateSpan(TraceContext traceContext, Labels labels) {
    tracer.annotateSpan(traceContext.getCurrent(), labels);
  }

  @Override
  public void setStackTrace(TraceContext traceContext, StackTrace stackTrace) {
    tracer.setStackTrace(traceContext.getCurrent(), stackTrace);
  }
}
