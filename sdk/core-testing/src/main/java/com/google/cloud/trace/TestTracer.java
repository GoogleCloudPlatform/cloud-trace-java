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
import com.google.cloud.trace.core.IdFactory;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import java.util.ArrayList;
import java.util.List;

public class TestTracer implements Tracer {
  private IdFactory<SpanId> spanIdFactory = new TestSpanIdFactory();
  private IdFactory<TraceId> traceIdFactory = new TestTraceIdFactory();

  public final List<StartSpanEvent> startSpanEvents = new ArrayList<StartSpanEvent>();
  public final List<EndSpanEvent> endSpanEvents = new ArrayList<EndSpanEvent>();
  public final List<AnnotateEvent> annotateEvents = new ArrayList<AnnotateEvent>();
  public final List<StackTraceEvent> stackTraceEvents = new ArrayList<StackTraceEvent>();


  @Override
  public TraceContext startSpan(String name) {
    return startSpan(name, null);
  }

  @Override
  public TraceContext startSpan(String name, StartSpanOptions options) {
    TraceContext context = createTraceContext();
    startSpanEvents.add(new StartSpanEvent(name, context, options));
    return context;
  }

  @Override
  public void endSpan(TraceContext traceContext) {
    endSpan(traceContext, null);
  }

  @Override
  public void endSpan(TraceContext traceContext, EndSpanOptions options) {
    endSpanEvents.add(new EndSpanEvent(traceContext, options));
  }

  @Override
  public void annotateSpan(TraceContext traceContext, Labels labels) {
    annotateEvents.add(new AnnotateEvent(traceContext, labels));
  }

  @Override
  public void setStackTrace(TraceContext traceContext, StackTrace stackTrace) {
    stackTraceEvents.add(new StackTraceEvent(traceContext, stackTrace));
  }

  public void reset() {
    startSpanEvents.clear();
    endSpanEvents.clear();
    annotateEvents.clear();
    stackTraceEvents.clear();
  }

  private TraceContext createTraceContext() {
    return new TraceContext(new TestSpanContextHandle(new SpanContext(traceIdFactory.nextId(),
        spanIdFactory.nextId(), new TraceOptions())));
  }

  public static class StartSpanEvent {
    private final String name;
    private final TraceContext traceContext;
    private final StartSpanOptions options;

    StartSpanEvent(String name, TraceContext traceContext, StartSpanOptions options) {
      this.name = name;
      this.traceContext = traceContext;
      this.options = options;
    }

    public String getName() {
      return name;
    }

    public TraceContext getTraceContext() {
      return traceContext;
    }

    public StartSpanOptions getOptions() {
      return options;
    }
  }

  public static class EndSpanEvent {
    private final TraceContext traceContext;
    private final EndSpanOptions endSpanOptions;

    EndSpanEvent(TraceContext traceContext, EndSpanOptions endSpanOptions) {
      this.traceContext = traceContext;
      this.endSpanOptions = endSpanOptions;
    }

    public TraceContext getTraceContext() {
      return traceContext;
    }

    public EndSpanOptions getEndSpanOptions() {
      return endSpanOptions;
    }
  }

  public static class AnnotateEvent {
    private final TraceContext traceContext;
    private final Labels labels;

    AnnotateEvent(TraceContext traceContext, Labels labels) {
      this.traceContext = traceContext;
      this.labels = labels;
    }

    public TraceContext getTraceContext() {
      return traceContext;
    }

    public Labels getLabels() {
      return labels;
    }
  }

  public static class StackTraceEvent {
    private final TraceContext traceContext;
    private final StackTrace stackTrace;

    StackTraceEvent(TraceContext traceContext, StackTrace stackTrace) {
      this.traceContext = traceContext;
      this.stackTrace = stackTrace;
    }

    public TraceContext getTraceContext() {
      return traceContext;
    }

    public StackTrace getStackTrace() {
      return stackTrace;
    }
  }
}
