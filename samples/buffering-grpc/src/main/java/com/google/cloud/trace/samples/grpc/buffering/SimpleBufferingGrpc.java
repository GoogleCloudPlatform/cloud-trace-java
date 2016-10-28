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

package com.google.cloud.trace.samples.grpc.buffering;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.trace.RawTracer;
import com.google.cloud.trace.TraceContextFactoryTracer;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.grpc.v1.GrpcTraceSink;
import com.google.cloud.trace.core.ConstantTraceOptionsFactory;
import com.google.cloud.trace.core.JavaTimestampFactory;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.ThrowableStackTraceHelper;
import com.google.cloud.trace.core.TimestampFactory;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.core.TraceContextFactory;
import com.google.cloud.trace.v1.RawTracerV1;
import com.google.cloud.trace.v1.sink.FlushableTraceSink;
import com.google.cloud.trace.v1.sink.SimpleBufferingTraceSink;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.cloud.trace.v1.source.TraceSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class SimpleBufferingGrpc {
  public static void main(String[] args) throws IOException {
    String projectId = System.getProperty("projectId");
    String clientSecretsFile = System.getProperty("clientSecretsFile");

    // Create the raw tracer.
    TraceSource traceSource = new TraceSource();
    TraceSink traceSink = new GrpcTraceSink("cloudtrace.googleapis.com",
        GoogleCredentials.fromStream(new FileInputStream(clientSecretsFile))
            .createScoped(Arrays.asList("https://www.googleapis.com/auth/trace.append")));
    FlushableTraceSink flushableSink = new SimpleBufferingTraceSink(traceSink);
    RawTracer rawTracer = new RawTracerV1(projectId, traceSource, flushableSink);

    // Create the tracer.
    TraceContextFactory traceContextFactory = new TraceContextFactory(
        new ConstantTraceOptionsFactory(true, false));
    TimestampFactory timestampFactory = new JavaTimestampFactory();
    Tracer tracer = new TraceContextFactoryTracer(rawTracer, traceContextFactory, timestampFactory);

    // Create a span using the given timestamps.
    TraceContext context1 = tracer.startSpan(traceContextFactory.initialContext(), "my span 1");

    TraceContext context2 = tracer.startSpan(context1, "my span 2");

    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    tracer.setStackTrace(context2, stackTraceBuilder.build());
    tracer.endSpan(context2);

    tracer.endSpan(context1);

    flushableSink.flush();
  }
}
