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

package com.google.cloud.trace.samples.logging.basic;

import com.google.cloud.trace.TraceSink;
import com.google.cloud.trace.TraceContextFactoryTracer;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.core.ConstantTraceOptionsFactory;
import com.google.cloud.trace.core.JavaTimestampFactory;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.ThrowableStackTraceHelper;
import com.google.cloud.trace.core.TimestampFactory;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.core.TraceContextFactory;
import com.google.cloud.trace.v1.TraceSinkV1;
import com.google.cloud.trace.v1.consumer.LoggingTraceConsumer;
import com.google.cloud.trace.v1.consumer.TraceConsumer;
import com.google.cloud.trace.v1.producer.TraceProducer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicLogging {
  private final static Logger logger = Logger.getLogger(BasicLogging.class.getName());

  public static void main(String[] args) {
    // Create the raw tracer.
    TraceProducer traceProducer = new TraceProducer();
    TraceConsumer traceConsumer = new LoggingTraceConsumer(logger, Level.WARNING);
    TraceSink traceSink = new TraceSinkV1("1", traceProducer, traceConsumer);

    // Create the tracer.
    TraceContextFactory traceContextFactory = new TraceContextFactory(
        new ConstantTraceOptionsFactory(true, false));
    TimestampFactory timestampFactory = new JavaTimestampFactory();
    Tracer tracer = new TraceContextFactoryTracer(traceSink, traceContextFactory, timestampFactory);

    // Create a span using the given timestamps.
    TraceContext context1 = tracer.startSpan(traceContextFactory.initialContext(), "my span 1");
    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    tracer.setStackTrace(context1, stackTraceBuilder.build());
    tracer.endSpan(context1);
  }
}
