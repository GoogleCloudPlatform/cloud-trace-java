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

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.trace.core.ConstantTraceOptionsFactory;
import com.google.cloud.trace.core.EndSpanOptions;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanContextFactory;
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.SpanKind;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.Timestamp;
import com.google.cloud.trace.core.TimestampFactory;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import com.google.cloud.trace.sink.TraceSink;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class SpanContextHandlerTracerTest {
  private SpanContextHandlerTracer tracer;
  private SpanContextHandler contextHandler;
  private TestTraceSink sink;

  @Before
  public void setup() {
    TimestampFactory tsFactory = new TestTimestampFactory(1, 2);
    this.sink = new TestTraceSink();
    SpanContextFactory contextFactory = new SpanContextFactory(new ConstantTraceOptionsFactory(true, true));
    this.contextHandler = new TestSpanContextHandler(contextFactory.initialContext());
    this.tracer = new SpanContextHandlerTracer(sink, contextHandler, contextFactory, tsFactory);
  }

  @Test
  public void testStartSpan() throws Exception {
    SpanContext initialContext = contextHandler.current();
    TraceContext traceContext = tracer.startSpan("foo");
    assertThat(sink.startSpanEvents).hasSize(1);
    TestTraceSink.StartSpanEvent startEvent1 = sink.startSpanEvents.get(0);

    assertThat(traceContext.getParent()).isEqualTo(initialContext);
    assertThat(traceContext.getParent()).isEqualTo(startEvent1.parentContext);

    assertThat(traceContext.getCurrent()).isNotEqualTo(traceContext.getParent());
    assertThat(traceContext.getCurrent()).isEqualTo(contextHandler.current());
    assertThat(traceContext.getCurrent()).isEqualTo(startEvent1.context);

    assertThat(startEvent1.name).isEqualTo("foo");
  }

  @Test
  public void testStartSpanWithOptions() {
    SpanContext initialContext = contextHandler.current();
    StartSpanOptions options = new StartSpanOptions();
    options.setSpanKind(SpanKind.RPC_CLIENT);
    Timestamp ts = new Timestamp() {
      @Override
      public long getSeconds() {
        return 1;
      }

      @Override
      public int getNanos() {
        return 2;
      }
    };
    options.setTimestamp(ts);
    TraceContext traceContext = tracer.startSpan("foo", options);
    assertThat(sink.startSpanEvents).hasSize(1);
    TestTraceSink.StartSpanEvent startEvent1 = sink.startSpanEvents.get(0);

    assertThat(traceContext.getParent()).isEqualTo(initialContext);
    assertThat(traceContext.getParent()).isEqualTo(startEvent1.parentContext);

    assertThat(traceContext.getCurrent()).isNotEqualTo(traceContext.getParent());
    assertThat(traceContext.getCurrent()).isEqualTo(contextHandler.current());
    assertThat(traceContext.getCurrent()).isEqualTo(startEvent1.context);

    assertThat(startEvent1.name).isEqualTo("foo");

    assertThat(startEvent1.kind).isEqualTo(SpanKind.RPC_CLIENT);
    assertThat(startEvent1.timestamp).isEqualTo(ts);
  }

  @Test
  public void testStartSpanWithTraceOptionsTrue() {
    SpanContext initial = new SpanContext(
        new TraceId(BigInteger.valueOf(1000)),
        new SpanId(2000),
        new TraceOptions());
    contextHandler.attach(initial);
    StartSpanOptions options = new StartSpanOptions()
        .setEnableTrace(true)
        .setEnableStackTrace(true);

    TraceContext traceContext = tracer.startSpan("foo", options);
    assertThat(sink.startSpanEvents).hasSize(1);
    TestTraceSink.StartSpanEvent startEvent1 = sink.startSpanEvents.get(0);

    assertThat(traceContext.getParent()).isEqualTo(initial);
    assertThat(traceContext.getParent()).isEqualTo(startEvent1.parentContext);

    assertThat(traceContext.getCurrent()).isNotEqualTo(traceContext.getParent());
    assertThat(traceContext.getCurrent()).isEqualTo(contextHandler.current());
    assertThat(traceContext.getCurrent()).isEqualTo(startEvent1.context);
    assertThat(traceContext.getCurrent().getTraceOptions().getTraceEnabled()).isEqualTo(true);
    assertThat(traceContext.getCurrent().getTraceOptions().getStackTraceEnabled()).isEqualTo(true);

    assertThat(startEvent1.name).isEqualTo("foo");
  }

  @Test
  public void testStartSpanWithTraceOptionsFalse() {
    SpanContext initial = new SpanContext(
        new TraceId(BigInteger.valueOf(1000)),
        new SpanId(2000),
        new TraceOptions(3));
    contextHandler.attach(initial);
    StartSpanOptions options = new StartSpanOptions()
        .setEnableTrace(false)
        .setEnableStackTrace(false);

    TraceContext traceContext = tracer.startSpan("foo", options);
    assertThat(sink.startSpanEvents).hasSize(1);
    TestTraceSink.StartSpanEvent startEvent1 = sink.startSpanEvents.get(0);

    assertThat(traceContext.getParent()).isEqualTo(initial);
    assertThat(traceContext.getParent()).isEqualTo(startEvent1.parentContext);

    assertThat(traceContext.getCurrent()).isNotEqualTo(traceContext.getParent());
    assertThat(traceContext.getCurrent()).isEqualTo(contextHandler.current());
    assertThat(traceContext.getCurrent()).isEqualTo(startEvent1.context);
    assertThat(traceContext.getCurrent().getTraceOptions().getTraceEnabled()).isEqualTo(false);
    assertThat(traceContext.getCurrent().getTraceOptions().getStackTraceEnabled()).isEqualTo(false);

    assertThat(startEvent1.name).isEqualTo("foo");
  }

  @Test
  public void testEndSpan() {
    SpanContext parent = new SpanContext(new TraceId(BigInteger.valueOf(1)), new SpanId(1), new TraceOptions());
    SpanContext child = new SpanContext(new TraceId(BigInteger.valueOf(1)), new SpanId(2), new TraceOptions());
    TraceContext inProgressContext = new TraceContext(child, parent);
    tracer.endSpan(inProgressContext);

    assertThat(contextHandler.current()).isEqualTo(parent);
    assertThat(sink.endSpanEvents).hasSize(1);
    TestTraceSink.EndSpanEvent endEvent = sink.endSpanEvents.get(0);
    assertThat(endEvent.context).isEqualTo(child);
  }

  @Test
  public void testEndSpanWithOptions() {
    EndSpanOptions options = new EndSpanOptions();
    Timestamp ts = new Timestamp() {
      @Override
      public long getSeconds() {
        return 1;
      }

      @Override
      public int getNanos() {
        return 2;
      }
    };
    options.setTimestamp(ts);
    SpanContext parent = new SpanContext(new TraceId(BigInteger.valueOf(1)), new SpanId(1), new TraceOptions());
    SpanContext child = new SpanContext(new TraceId(BigInteger.valueOf(1)), new SpanId(2), new TraceOptions());
    TraceContext inProgressContext = new TraceContext(child, parent);
    tracer.endSpan(inProgressContext, options);

    assertThat(sink.endSpanEvents).hasSize(1);
    TestTraceSink.EndSpanEvent endEvent = sink.endSpanEvents.get(0);
    assertThat(contextHandler.current()).isEqualTo(parent);
    assertThat(endEvent.context).isEqualTo(child);
    assertThat(endEvent.timestamp).isEqualTo(ts);
  }

  @Test
  public void testAnnotateSpan() {
    SpanContext parent = new SpanContext(new TraceId(BigInteger.valueOf(1)), new SpanId(1), new TraceOptions());
    SpanContext child = new SpanContext(new TraceId(BigInteger.valueOf(1)), new SpanId(2), new TraceOptions());
    TraceContext inProgressContext = new TraceContext(child, parent);
    Labels labels = Labels.builder().add("foo", "bar").build();
    tracer.annotateSpan(inProgressContext, labels);
    assertThat(sink.annotateEvents).hasSize(1);
    TestTraceSink.AnnotateEvent annotateEvent = sink.annotateEvents.get(0);
    assertThat(annotateEvent.context).isEqualTo(child);
    assertThat(annotateEvent.labels).isEqualTo(labels);
  }

  @Test
  public void testSetStackTrace() {
    SpanContext parent = new SpanContext(new TraceId(BigInteger.valueOf(1)), new SpanId(1), new TraceOptions());
    SpanContext child = new SpanContext(new TraceId(BigInteger.valueOf(1)), new SpanId(2), new TraceOptions());
    TraceContext inProgressContext = new TraceContext(child, parent);
    StackTrace stackTrace = StackTrace.builder()
        .add("testClass", "testMethod", "testFile", 1, 2).build();
    tracer.setStackTrace(inProgressContext, stackTrace);
    assertThat(sink.stackTraceEvents).hasSize(1);
    TestTraceSink.StackTraceEvent annotateEvent = sink.stackTraceEvents.get(0);
    assertThat(annotateEvent.context).isEqualTo(child);
    assertThat(annotateEvent.stackTrace).isEqualTo(stackTrace);
  }

  private static class TestTraceSink implements TraceSink {
    final List<StartSpanEvent> startSpanEvents = new ArrayList<StartSpanEvent>();
    final List<EndSpanEvent> endSpanEvents = new ArrayList<EndSpanEvent>();
    final List<AnnotateEvent> annotateEvents = new ArrayList<AnnotateEvent>();
    final List<StackTraceEvent> stackTraceEvents = new ArrayList<StackTraceEvent>();

    @Override
    public void startSpan(SpanContext context, SpanContext parentContext, SpanKind spanKind,
        String name, Timestamp timestamp) {
      startSpanEvents.add(new StartSpanEvent(context, parentContext, spanKind, name, timestamp));
    }

    @Override
    public void endSpan(SpanContext context, Timestamp timestamp) {
      endSpanEvents.add(new EndSpanEvent(context, timestamp));
    }

    @Override
    public void annotateSpan(SpanContext context, Labels labels) {
      annotateEvents.add(new AnnotateEvent(context, labels));
    }

    @Override
    public void setStackTrace(SpanContext context, StackTrace stackTrace) {
      stackTraceEvents.add(new StackTraceEvent(context, stackTrace));
    }

    static class StartSpanEvent {
      final SpanContext context, parentContext;
      final SpanKind kind;
      final String name;
      final Timestamp timestamp;

      StartSpanEvent(SpanContext context, SpanContext parentContext,
          SpanKind kind, String name, Timestamp timestamp) {
        this.context = context;
        this.parentContext = parentContext;
        this.kind = kind;
        this.name = name;
        this.timestamp = timestamp;
      }
    }

    static class EndSpanEvent {
      final SpanContext context;
      final Timestamp timestamp;

      EndSpanEvent(SpanContext context, Timestamp timestamp) {
        this.context = context;
        this.timestamp = timestamp;
      }
    }

    static class AnnotateEvent {
      final SpanContext context;
      final Labels labels;

      AnnotateEvent(SpanContext context, Labels labels) {
        this.context = context;
        this.labels = labels;
      }
    }

    static class StackTraceEvent {
      final SpanContext context;
      final StackTrace stackTrace;

      StackTraceEvent(SpanContext context, StackTrace stackTrace) {
        this.context = context;
        this.stackTrace = stackTrace;
      }
    }
  }

  private static class TestTimestampFactory implements TimestampFactory {
    long seconds;
    int nanos;

    TestTimestampFactory(long seconds, int nanos) {
      this.seconds = seconds;
      this.nanos = nanos;
    }

    @Override
    public Timestamp now() {
      return new Timestamp() {
        @Override
        public long getSeconds() {
          return seconds;
        }

        @Override
        public int getNanos() {
          return nanos;
        }
      };
    }
  }

}
