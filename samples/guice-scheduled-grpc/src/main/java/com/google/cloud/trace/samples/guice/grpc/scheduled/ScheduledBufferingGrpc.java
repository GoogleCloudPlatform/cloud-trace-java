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

package com.google.cloud.trace.samples.guice.grpc.scheduled;

import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.ThrowableStackTraceHelper;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.guice.ConstantTraceOptionsFactoryModule;
import com.google.cloud.trace.guice.GrpcSpanContextHandlerModule;
import com.google.cloud.trace.guice.JavaTimestampFactoryModule;
import com.google.cloud.trace.guice.SingleThreadScheduledExecutorModule;
import com.google.cloud.trace.guice.StackTraceDisabledModule;
import com.google.cloud.trace.guice.SpanContextHandlerTracerModule;
import com.google.cloud.trace.guice.TraceEnabledModule;
import com.google.cloud.trace.guice.auth.ClientSecretsFilePropertiesModule;
import com.google.cloud.trace.guice.auth.ClientSecretsGoogleCredentialsModule;
import com.google.cloud.trace.guice.auth.TraceAppendScopesModule;
import com.google.cloud.trace.guice.grpc.v1.GrpcTraceSinkModule;
import com.google.cloud.trace.guice.v1.ProjectIdPropertiesModule;
import com.google.cloud.trace.guice.v1.RoughTraceSizerModule;
import com.google.cloud.trace.guice.v1.ScheduledBufferingTraceSinkModule;
import com.google.cloud.trace.guice.v1.SinkBufferSizePropertiesModule;
import com.google.cloud.trace.guice.v1.SinkScheduledDelayPropertiesModule;
import com.google.cloud.trace.guice.v1.TraceSinkV1Module;
import com.google.cloud.trace.v1.consumer.FlushableTraceConsumer;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ScheduledBufferingGrpc {
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(
        new SpanContextHandlerTracerModule(),
        new JavaTimestampFactoryModule(),
        new GrpcSpanContextHandlerModule(),
        new TraceSinkV1Module(),
        new ConstantTraceOptionsFactoryModule(),
        new ProjectIdPropertiesModule(),
        new ScheduledBufferingTraceSinkModule(),
        new TraceEnabledModule(),
        new StackTraceDisabledModule(),
        new GrpcTraceSinkModule(),
        new RoughTraceSizerModule(),
        new SinkBufferSizePropertiesModule(),
        new SinkScheduledDelayPropertiesModule(),
        new SingleThreadScheduledExecutorModule(),
        new ClientSecretsGoogleCredentialsModule(),
        new ClientSecretsFilePropertiesModule(),
        new TraceAppendScopesModule());

    Tracer tracer = injector.getInstance(Tracer.class);

    TraceContext context1 = tracer.startSpan("my span 1",
        new StartSpanOptions().setEnableTrace(true));
    TraceContext context2 = tracer.startSpan("my span 2");

    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    tracer.setStackTrace(context2, stackTraceBuilder.build());
    tracer.endSpan(context1);

    tracer.endSpan(context1);

    FlushableTraceConsumer flushableSink = injector.getInstance(FlushableTraceConsumer.class);

    flushableSink.flush();
  }
}
