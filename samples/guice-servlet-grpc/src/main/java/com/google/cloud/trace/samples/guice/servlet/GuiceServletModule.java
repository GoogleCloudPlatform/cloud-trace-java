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

import com.google.auth.Credentials;
import com.google.cloud.logging.LoggingEnhancer;
import com.google.cloud.logging.LoggingHandler;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.trace.SpanContextHandler;
import com.google.cloud.trace.core.SpanContextFactory;
import com.google.cloud.trace.core.TraceOptionsFactory;
import com.google.cloud.trace.guice.auth.Scopes;
import com.google.cloud.trace.guice.servlet.RequestTraceContextFilter;
import com.google.cloud.trace.guice.v1.ProjectId;
import com.google.cloud.trace.sink.TraceSink;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GuiceServletModule extends ServletModule {
  @Override
  protected void configureServlets() {
    bind(GuiceServlet.class).in(Singleton.class);
    filter("/*").through(RequestTraceContextFilter.class);
    serve("/*").with(GuiceServlet.class);
  }

  @Provides
  @Singleton
  SpanContextFactory provideSpanContextFactory(TraceOptionsFactory traceOptionsFactory) {
    return new SpanContextFactory(traceOptionsFactory);
  }

  @Provides
  @Singleton
  TraceSink provideTraceSink(Set<TraceSink> traceSinkSet) {
    return traceSinkSet.toArray(new TraceSink[0])[0];
  }

  @Provides
  @Singleton
  LoggingEnhancer provideLoggingEnhancer(
      SpanContextHandler spanContextHandler, @ProjectId String projectId) {
    return new SpanContextLoggingEnhancer(spanContextHandler, projectId);
  }

  @Provides
  @Scopes
  @Singleton
  List<String> provideScopes() {
    return Arrays.asList(
        "https://www.googleapis.com/auth/trace.append",
        "https://www.googleapis.com/auth/logging.write");
  }

  @Provides
  @Singleton
  LoggingOptions provideLoggingOptions(Credentials credentials, @ProjectId String projectId) {
    return LoggingOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build();
  }

  @Provides
  @Singleton
  LoggingHandler provideLoggingHandler(
      LoggingOptions loggingOptions, LoggingEnhancer loggingEnhancer) {
    return new LoggingHandler(
        null, loggingOptions, null, Collections.singletonList(loggingEnhancer));
  }
}
