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

import com.google.cloud.trace.GrpcSpanContextHandler;
import com.google.cloud.trace.ManagedTracer;
import com.google.cloud.trace.SpanContextFactoryTracer;
import com.google.cloud.trace.SpanContextHandler;
import com.google.cloud.trace.SpanContextHandlerTracer;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.core.ConstantTraceOptionsFactory;
import com.google.cloud.trace.core.JavaTimestampFactory;
import com.google.cloud.trace.core.SpanContextFactory;
import com.google.cloud.trace.core.TimestampFactory;
import com.google.cloud.trace.core.TraceSink;
import com.google.cloud.trace.v1.TraceSinkV1;
import com.google.cloud.trace.v1.consumer.LoggingTraceConsumer;
import com.google.cloud.trace.v1.consumer.TraceConsumer;
import com.google.cloud.trace.v1.producer.TraceProducer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that provides a logging trace service.
 */
public class LoggingTraceService implements TraceService {
  private final static Logger logger = Logger.getLogger(LoggingTraceService.class.getName());
  
  private final ManagedTracer tracer;
  private final SpanContextHandler spanContextHandler;

  public LoggingTraceService() {
    // Create the trace sink.
    TraceProducer traceProducer = new TraceProducer();
    TraceConsumer traceConsumer = new LoggingTraceConsumer(logger, Level.WARNING);
    TraceSink traceSink = new TraceSinkV1("1", traceProducer, traceConsumer);

    // Create the tracer.
    SpanContextFactory spanContextFactory = new SpanContextFactory(
        new ConstantTraceOptionsFactory(true, false));
    TimestampFactory timestampFactory = new JavaTimestampFactory();

    // Create the services.
    this.spanContextHandler = new GrpcSpanContextHandler(spanContextFactory.initialContext());
    this.tracer = new SpanContextHandlerTracer(traceSink, spanContextHandler, spanContextFactory, timestampFactory);
  }

  @Override
  public ManagedTracer getTracer() {
    return tracer;
  }

  @Override
  public SpanContextHandler getSpanContextHandler() {
    return spanContextHandler;
  }
}
