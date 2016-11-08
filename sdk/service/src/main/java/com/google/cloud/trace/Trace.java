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
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import com.google.cloud.trace.service.TraceService;
import java.util.ServiceLoader;

/**
 * A class that provides trace services. This class uses a ServiceLoader to locate instances of
 * {@link TraceService}.
 */
public class Trace {
  private static final TraceService service;

  static {
    TraceService traceService = null;
    for (TraceService candidate : ServiceLoader.load(TraceService.class)) {
      if (traceService != null) {
        throw new RuntimeException("Found more than one TraceService provider.");
      }
      traceService = candidate;
    }
    if (traceService == null) {
      service = new TraceService() {
        private final SpanContext spanContext = new SpanContext(
            TraceId.invalid(), SpanId.invalid(), new TraceOptions());
        private final TraceContext traceContext = new TraceContext(spanContext, null);
        private final ManagedTracer tracer = new NoTracer(traceContext);
        private final SpanContextHandler spanContextHandler = new NoSpanContextHandler(spanContext);
        @Override
        public ManagedTracer getTracer() {
          return tracer;
        }
        @Override
        public SpanContextHandler getSpanContextHandler() {
          return spanContextHandler;
        }
      };
    } else {
      service = traceService;
    }
  }

  public static ManagedTracer getTracer() {
    return service.getTracer();
  }

  public static SpanContextHandler getSpanContextHandler() {
    return service.getSpanContextHandler();
  }

  private static class NoTracer implements ManagedTracer {
    private final TraceContext context;
    private NoTracer(TraceContext context) {
      this.context = context;
    }
    @Override
    public TraceContext startSpan(String name) {
      return context;
    }
    @Override
    public TraceContext startSpan(String name, StartSpanOptions options) {
      return context;
    }
    @Override
    public void endSpan(TraceContext traceContext) {}
    @Override
    public void endSpan(TraceContext traceContext, EndSpanOptions options) {}
    @Override
    public void annotateSpan(TraceContext traceContext, Labels labels) {}
    @Override
    public void setStackTrace(TraceContext traceContext, StackTrace stackTrace) {}
  }

  private static class NoSpanContextHandler implements SpanContextHandler {
    private final SpanContext context;
    private NoSpanContextHandler(SpanContext context) {
      this.context = context;
    }
    @Override
    public SpanContext current() {
      return context;
    }
    @Override
    public SpanContext attach(SpanContext context) {
      return context;
    }
    @Override
    public void detach(SpanContext toAttach) {}
  }
}
