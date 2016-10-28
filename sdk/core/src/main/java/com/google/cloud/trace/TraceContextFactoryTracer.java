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
import com.google.cloud.trace.core.SpanKind;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.Timestamp;
import com.google.cloud.trace.core.TimestampFactory;
import com.google.cloud.trace.core.SpanContextFactory;
import com.google.cloud.trace.core.TraceOptions;
import com.google.cloud.trace.core.TraceSink;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

/**
 * A tracer used to trace application code.
 *
 * <p>This tracer sends trace data out to a set of raw tracers. It uses a span context factory to
 * generate a new span context from a parent span context when starting a new span, and it uses a
 * timestamp factory to generate start and end timestamps for spans.
 *
 * @see TraceSink
 * @see Timestamp
 * @see TimestampFactory
 * @see SpanContext
 * @see SpanContextFactory
 * @see Tracer
 */
public class TraceContextFactoryTracer implements Tracer {
  private final ImmutableSet<TraceSink> tracers;
  private final SpanContextFactory spanContextFactory;
  private final TimestampFactory timestampFactory;

  /**
   * Creates a new tracer.
   *
   * @param tracers             a set of raw tracers that this tracer will send trace data to.
   * @param spanContextFactory a span context factory used to generate new span contexts.
   * @param timestampFactory    a timestamp factory used to generate new timestamps.
   */
  public TraceContextFactoryTracer(Set<TraceSink> tracers, SpanContextFactory spanContextFactory,
      TimestampFactory timestampFactory) {
    this.tracers = ImmutableSet.copyOf(tracers);
    this.spanContextFactory = spanContextFactory;
    this.timestampFactory = timestampFactory;
  }

  /**
   * Creates a new tracer.
   *
   * @param tracer              a raw tracer that this tracer will send trace data to.
   * @param spanContextFactory a span context factory used to generate new span contexts.
   * @param timestampFactory    a timestamp factory used to generate new timestamps.
   */
  public TraceContextFactoryTracer(TraceSink tracer, SpanContextFactory spanContextFactory,
      TimestampFactory timestampFactory) {
    this(ImmutableSet.of(tracer), spanContextFactory, timestampFactory);
  }

  @Override
  public SpanContext startSpan(SpanContext parentContext, String name) {
    return startSpanOptions(parentContext, name, null, null, null);
  }

  @Override
  public SpanContext startSpan(SpanContext parentContext, String name, StartSpanOptions options) {
    return startSpanOptions(parentContext, name, options.getTimestamp(), options.getSpanKind(),
        options.getTraceOptions());
  }

  @Override
  public void endSpan(SpanContext context) {
    endSpanOptions(context, null);
  }

  @Override
  public void endSpan(SpanContext context, EndSpanOptions options) {
    endSpanOptions(context, options.getTimestamp());
  }

  @Override
  public void annotateSpan(SpanContext context, Labels labels) {
    for (TraceSink tracer : tracers) {
      tracer.annotateSpan(context, labels);
    }
  }

  @Override
  public void setStackTrace(SpanContext context, StackTrace stackTrace) {
    for (TraceSink tracer : tracers) {
      tracer.setStackTrace(context, stackTrace);
    }
  }

  private SpanContext startSpanOptions(SpanContext parentContext, String name, Timestamp timestamp,
      SpanKind spanKind, TraceOptions traceOptions) {
    if (timestamp == null) {
      timestamp = timestampFactory.now();
    }
    if (spanKind == null) {
      spanKind = SpanKind.UNSPECIFIED;
    }
    if (traceOptions != null) {
      parentContext = parentContext.overrideOptions(traceOptions);
    }
    SpanContext context = spanContextFactory.childContext(parentContext);
    for (TraceSink tracer : tracers) {
      tracer.startSpan(context, parentContext, spanKind, name, timestamp);
    }
    return context;
  }

  private void endSpanOptions(SpanContext context, Timestamp timestamp) {
    if (timestamp == null) {
      timestamp = timestampFactory.now();
    }
    for (TraceSink tracer : tracers) {
      tracer.endSpan(context, timestamp);
    }
  }
}
