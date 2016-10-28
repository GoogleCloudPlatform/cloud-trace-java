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

package com.google.cloud.trace.samples.grpc.managed;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.trace.core.DefaultTraceContextHandler;
import com.google.cloud.trace.ManagedTracer;
import com.google.cloud.trace.RawTracer;
import com.google.cloud.trace.TraceContextFactoryTracer;
import com.google.cloud.trace.core.TraceContextHandler;
import com.google.cloud.trace.TraceContextHandlerTracer;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.grpc.v1.GrpcTraceSink;
import com.google.cloud.trace.core.ConstantTraceOptionsFactory;
import com.google.cloud.trace.core.JavaTimestampFactory;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.ThrowableStackTraceHelper;
import com.google.cloud.trace.core.TimestampFactory;
import com.google.cloud.trace.core.TraceContextFactory;
import com.google.cloud.trace.v1.RawTracerV1;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.cloud.trace.v1.source.TraceSource;

import java.io.IOException;

public class ManagedGrpc {
  public static void main(String[] args) throws IOException {
    String projectId = System.getProperty("projectId");

    // Create the raw tracer.
    TraceSource traceSource = new TraceSource();
    TraceSink traceSink = new GrpcTraceSink("cloudtrace.googleapis.com",
        GoogleCredentials.getApplicationDefault());
    RawTracer rawTracer = new RawTracerV1(projectId, traceSource, traceSink);

    // Create the tracer.
    TraceContextFactory traceContextFactory = new TraceContextFactory(
        new ConstantTraceOptionsFactory(true, false));
    TimestampFactory timestampFactory = new JavaTimestampFactory();
    Tracer tracer = new TraceContextFactoryTracer(rawTracer, traceContextFactory, timestampFactory);

    // Create the managed tracer.
    TraceContextHandler traceContextHandler = new DefaultTraceContextHandler(
        traceContextFactory.initialContext());
    ManagedTracer managedTracer = new TraceContextHandlerTracer(tracer, traceContextHandler);

    // Create some trace data.
    managedTracer.startSpan("my span 1");

    managedTracer.startSpan("my span 2");

    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    managedTracer.setStackTrace(stackTraceBuilder.build());
    managedTracer.endSpan();

    managedTracer.endSpan();
  }
}
