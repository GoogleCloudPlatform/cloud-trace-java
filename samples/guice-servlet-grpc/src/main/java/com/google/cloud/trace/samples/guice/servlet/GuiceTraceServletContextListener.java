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

package com.google.cloud.trace.samples.guice.servlet;

import com.google.cloud.trace.guice.GrpcSpanContextHandlerModule;
import com.google.cloud.trace.guice.JavaTimestampFactoryModule;
import com.google.cloud.trace.guice.NaiveSamplingRateJndiModule;
import com.google.cloud.trace.guice.NaiveSamplingTraceOptionsFactoryModule;
import com.google.cloud.trace.guice.SingleThreadScheduledExecutorModule;
import com.google.cloud.trace.guice.StackTraceDisabledModule;
import com.google.cloud.trace.guice.SpanContextHandlerTracerModule;
import com.google.cloud.trace.guice.annotation.TracerSpanModule;
import com.google.cloud.trace.guice.api.ApiHostModule;
import com.google.cloud.trace.guice.auth.ClientSecretsFileJndiModule;
import com.google.cloud.trace.guice.auth.ClientSecretsGoogleCredentialsModule;
import com.google.cloud.trace.guice.auth.TraceAppendScopesModule;
import com.google.cloud.trace.guice.grpc.v1.GrpcTraceSinkModule;

import com.google.cloud.trace.guice.servlet.RequestLabelerModule;
import com.google.cloud.trace.guice.v1.ProjectIdJndiModule;
import com.google.cloud.trace.guice.v1.RoughTraceSizerModule;
import com.google.cloud.trace.guice.v1.ScheduledBufferingTraceSinkModule;
import com.google.cloud.trace.guice.v1.SinkBufferSizeJndiModule;
import com.google.cloud.trace.guice.v1.SinkScheduledDelayJndiModule;
import com.google.cloud.trace.guice.v1.TraceSinkV1Module;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceTraceServletContextListener extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(
        new SpanContextHandlerTracerModule(),
        new JavaTimestampFactoryModule(),
        new GrpcSpanContextHandlerModule(),
        new TraceSinkV1Module(),
        new NaiveSamplingTraceOptionsFactoryModule(),
        new ProjectIdJndiModule(),
        new ScheduledBufferingTraceSinkModule(),
        new NaiveSamplingRateJndiModule(),
        new StackTraceDisabledModule(),
        new GrpcTraceSinkModule(),
        new RoughTraceSizerModule(),
        new SinkBufferSizeJndiModule(),
        new SinkScheduledDelayJndiModule(),
        new SingleThreadScheduledExecutorModule(),
        new ApiHostModule(),
        new ClientSecretsGoogleCredentialsModule(),
        new ClientSecretsFileJndiModule(),
        new TraceAppendScopesModule(),
        new TracerSpanModule(),
        new RequestLabelerModule(),
        new GuiceServletModule());
  }
}
