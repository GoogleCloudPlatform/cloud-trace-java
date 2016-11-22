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

package com.google.cloud.trace.service;

import com.google.appengine.api.labs.trace.Span;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.core.EndSpanOptions;
import com.google.cloud.trace.core.Label;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.TraceContext;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of {@link Tracer} based on Google App Engine's Trace Service
 * API.
 */
class AppEngineTracer implements Tracer {
  private final com.google.appengine.api.labs.trace.TraceService traceService;

  AppEngineTracer(com.google.appengine.api.labs.trace.TraceService traceService) {
    this.traceService = checkNotNull(traceService);
  }

  @Override
  public TraceContext startSpan(String name) {
    checkNotNull(name);

    return new AppEngineTraceContext(traceService.startSpan(name));
  }

  @Override
  public TraceContext startSpan(String name, StartSpanOptions options) {
    throw new UnsupportedOperationException(
        "#startSpan(String, StartSpanOptions) is not supported because "
        + "the App Engine Trace Service API does not allow for overriding "
        + "the start timestamp and span kind");
  }

  @Override
  public void endSpan(TraceContext traceContext) {
    checkNotNull(traceContext);

    getSpan(traceContext).endSpan();
  }

  @Override
  public void endSpan(TraceContext traceContext, EndSpanOptions options) {
    throw new UnsupportedOperationException(
        "#endSpan(String, EndSpanOptions) is not supported because "
        + "the App Engine Trace Service API does not allow for overriding "
        + "the end timestamp and span kind");
  }

  @Override
  public void annotateSpan(TraceContext traceContext, Labels labels) {
    checkNotNull(traceContext);
    checkNotNull(labels);

    Span span = getSpan(traceContext);
    for (Label label : labels.getLabels()) {
      span.setLabel(label.getKey(), label.getValue());
    }
  }

  @Override
  public void setStackTrace(TraceContext traceContext, StackTrace stackTrace) {
    // TODO: Implement this if still needed after refactorings are complete.
  }

  private Span getSpan(TraceContext traceContext) {
    checkNotNull(traceContext);

    return ((AppEngineTraceContext) traceContext).getSpan();
  }
}
