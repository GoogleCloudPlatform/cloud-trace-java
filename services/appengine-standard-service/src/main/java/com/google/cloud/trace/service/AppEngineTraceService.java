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

import com.google.appengine.api.labs.trace.TraceServiceFactory;
import com.google.cloud.trace.SpanContextHandler;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.core.SpanContextFactory;
import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of {@link TraceService} based on Google App Engine's Trace
 * Service API.
 */
public class AppEngineTraceService implements TraceService {
  private final Tracer tracer;

  public AppEngineTraceService() {
    this(TraceServiceFactory.getTraceService());
  }

  @VisibleForTesting
  AppEngineTraceService(com.google.appengine.api.labs.trace.TraceService traceService) {
    checkNotNull(traceService);

    this.tracer = new AppEngineTracer(traceService);
  }

  @Override
  public Tracer getTracer() {
    return tracer;
  }

  @Override
  public SpanContextHandler getSpanContextHandler() {
    throw new UnsupportedOperationException(
        "#getSpanContextHandler is not supported because the context "
        + "is managed by the App Engine runtime");
  }

  @Override
  public SpanContextFactory getSpanContextFactory() {
    throw new UnsupportedOperationException(
        "#getSpanContextFactory is not supported because the context "
            + "is managed by the App Engine runtime");
  }
}
