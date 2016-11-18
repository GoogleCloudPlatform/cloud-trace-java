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
import com.google.appengine.api.labs.trace.TraceServiceFactory;
import com.google.apphosting.api.CloudTraceContext;
import com.google.cloud.trace.SpanContextHandler;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.core.EndSpanOptions;
import com.google.cloud.trace.core.Label;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.TraceContext;
import com.google.common.annotations.VisibleForTesting;

/**
 * Implementation of {@link TraceService} based on Google App Engine's Trace
 * Service API.
 */
public class AppEngineTraceService implements TraceService {

  private final Tracer tracer;

  public AppEngineTraceService() {
    this(TraceServiceFactory.getTraceService());
  }

  @VisibleForTesting
  AppEngineTraceService(com.google.appengine.api.labs.trace.TraceService appEngineTraceService) {
    this.tracer = new AppEngineTracer(appEngineTraceService);
  }

  @Override
  public Tracer getTracer() {
    return tracer;
  }

  @Override
  public SpanContextHandler getSpanContextHandler() {
    throw new UnsupportedOperationException(
            "#getSpanContextHandler is not supported because the context "
            + "is managed by the App Engine runtime");
  }

  private static class AppEngineTracer implements Tracer {

    private final com.google.appengine.api.labs.trace.TraceService appEngineTraceService;

    private AppEngineTracer(com.google.appengine.api.labs.trace.TraceService appEngineTraceService) {
      this.appEngineTraceService = appEngineTraceService;
    }

    @Override
    public TraceContext startSpan(String name) {
      return new AppEngineTraceContext(appEngineTraceService.startSpan(name));
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
      Span span = getSpan(traceContext);
      for (Label label : labels.getLabels()) {
        span.setLabel(label.getKey(), label.getValue());
      }
    }

    @Override
    public void setStackTrace(TraceContext traceContext, StackTrace stackTrace) {
      // TODO: Implement this.
    }

    private Span getSpan(TraceContext traceContext) {
      return ((AppEngineTraceContext) traceContext).getSpan();
    }
  }

  private static class AppEngineTraceContext extends TraceContext {

    private final Span span;

    AppEngineTraceContext(Span span) {
      super(getSpanContext(span.getContext()), getSpanContext(span.getParentContext()));
      this.span = span;
    }

    Span getSpan() {
      return span;
    }

    private static SpanContext getSpanContext(CloudTraceContext cloudTraceContext) {
      // TODO: Implement this.
      return null;
    }
  }
}
