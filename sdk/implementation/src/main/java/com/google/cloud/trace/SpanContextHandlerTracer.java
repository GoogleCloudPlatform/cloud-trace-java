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
import com.google.cloud.trace.core.SpanContextFactory;
import com.google.cloud.trace.core.SpanKind;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.Timestamp;
import com.google.cloud.trace.core.TimestampFactory;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.core.TraceOptions;
import com.google.cloud.trace.core.TraceSink;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

/**
 * A tracer that maintains trace context.
 *
 * <p>This tracer maintains the current trace context and delegates calls to another tracer.</p>
 */
public class SpanContextHandlerTracer implements Tracer {
  private final SpanContextHandler contextHandler;
  private final TimestampFactory timestampFactory;
  private final ImmutableSet<TraceSink> sinks;
  private final SpanContextFactory spanContextFactory;

  /**
   * Creates a new tracer.
   *
   * @param sinks a set of trace sinks that this tracer will send trace data to.
   * @param contextHandler a span context handler that manages the current span context.
   * @param spanContextFactory a span context factory used to generate new span contexts.
   * @param timestampFactory a timestamp factory used to generate new timestamps.
   */
  public SpanContextHandlerTracer(Set<TraceSink> sinks, SpanContextHandler contextHandler, SpanContextFactory spanContextFactory, TimestampFactory timestampFactory) {
    this.contextHandler = contextHandler;
    this.timestampFactory = timestampFactory;
    this.sinks = ImmutableSet.copyOf(sinks);
    this.spanContextFactory = spanContextFactory;
  }

  /**
   * Creates a new tracer.
   * @param sink a trace sink that this tracer will send trace data to.
   * @param contextHandler a span context handler that manages the current span context.
   * @param spanContextFactory a span context factory used to generate new span contexts.
   * @param timestampFactory a timestamp factory used to generate new timestamps.
   */
  public SpanContextHandlerTracer(TraceSink sink, SpanContextHandler contextHandler, SpanContextFactory spanContextFactory, TimestampFactory timestampFactory) {
    this(ImmutableSet.of(sink), contextHandler, spanContextFactory, timestampFactory);
  }

  @Override
  public TraceContext startSpan(String name) {
    SpanContext parent = contextHandler.current();
    SpanContext child = startSpanOptions(parent, name, null, null, null, null);
    contextHandler.attach(child);
    return new TraceContext(child, parent);
  }

  @Override
  public TraceContext startSpan(String name, StartSpanOptions options) {
    SpanContext parent = contextHandler.current();
    SpanContext child = startSpanOptions(parent, name, options.getTimestamp(), options.getSpanKind(),
        options.getEnableTrace(), options.getEnableStackTrace());
    contextHandler.attach(child);
    return new TraceContext(child, parent);
  }

  @Override
  public void endSpan(TraceContext traceContext) {
    endSpanOptions(traceContext.getCurrent(), null);
    contextHandler.detach(traceContext.getParent());
  }

  @Override
  public void endSpan(TraceContext traceContext, EndSpanOptions options) {
    endSpanOptions(traceContext.getCurrent(), options.getTimestamp());
    contextHandler.detach(traceContext.getParent());
  }

  @Override
  public void annotateSpan(TraceContext traceContext, Labels labels) {
    for (TraceSink sink : sinks) {
      sink.annotateSpan(traceContext.getCurrent(), labels);
    }
  }

  @Override
  public void setStackTrace(TraceContext traceContext, StackTrace stackTrace) {
    for (TraceSink sink : sinks) {
      sink.setStackTrace(traceContext.getCurrent(), stackTrace);
    }
  }

  private SpanContext startSpanOptions(SpanContext parentContext, String name, Timestamp timestamp,
      SpanKind spanKind, Boolean enableTrace, Boolean enableStackTrace) {
    if (timestamp == null) {
      timestamp = timestampFactory.now();
    }
    if (spanKind == null) {
      spanKind = SpanKind.UNSPECIFIED;
    }
    SpanContext context;
    if (enableTrace != null || enableStackTrace != null) {
      TraceOptions options = parentContext.getTraceOptions();
      if (enableTrace != null) {
        options = options.overrideTraceEnabled(enableTrace);
      }
      if (enableStackTrace != null) {
        options = options.overrideStackTraceEnabled(enableStackTrace);
      }
      context = spanContextFactory.childContext(parentContext.overrideOptions(options));
    } else {
      context = spanContextFactory.childContext(parentContext);
    }
    for (TraceSink sink : sinks) {
      sink.startSpan(context, parentContext, spanKind, name, timestamp);
    }
    return context;
  }

  private void endSpanOptions(SpanContext context, Timestamp timestamp) {
    if (timestamp == null) {
      timestamp = timestampFactory.now();
    }
    for (TraceSink sink : sinks) {
      sink.endSpan(context, timestamp);
    }
  }
}
