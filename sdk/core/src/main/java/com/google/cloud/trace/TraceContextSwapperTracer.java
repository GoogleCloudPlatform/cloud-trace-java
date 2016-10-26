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

public class TraceContextSwapperTracer implements ManagedTracer {
  private final Tracer tracer;
  private final TraceContextSwapper contextSwapper;

  public TraceContextSwapperTracer(Tracer tracer, TraceContextSwapper contextSwapper) {
    this.tracer = tracer;
    this.contextSwapper = contextSwapper;
  }

  @Override
  public void startSpan(String name) {
    TraceContextHandler contextHandler = getContextHandler();
    TraceContext context = tracer.startSpan(contextHandler.current(), name);
    contextHandler.push(context);
  }

  @Override
  public void startSpan(String name, StartSpanOptions options) {
    TraceContextHandler contextHandler = getContextHandler();
    TraceContext context = tracer.startSpan(contextHandler.current(), name, options);
    contextHandler.push(context);
  }

  @Override
  public void endSpan() {
    TraceContextHandler contextHandler = getContextHandler();
    tracer.endSpan(contextHandler.current());
    contextHandler.pop();
  }

  @Override
  public void endSpan(EndSpanOptions options) {
    TraceContextHandler contextHandler = getContextHandler();
    tracer.endSpan(contextHandler.current(), options);
    contextHandler.pop();
  }

  @Override
  public void annotateSpan(Labels labels) {
    tracer.annotateSpan(getContextHandler().current(), labels);
  }

  @Override
  public void setStackTrace(StackTrace stackTrace) {
    tracer.setStackTrace(getContextHandler().current(), stackTrace);
  }

  @Override
  public TraceContext getCurrentTraceContext() {
    return getContextHandler().current();
  }

  private TraceContextHandler getContextHandler() {
    return contextSwapper.currentHandler();
  }
}
