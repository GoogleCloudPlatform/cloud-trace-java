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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.trace.GrpcSpanContextHandler;
import com.google.cloud.trace.SpanContextHandler;
import com.google.cloud.trace.SpanContextHandlerTracer;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.core.JavaTimestampFactory;
import com.google.cloud.trace.core.RateLimitingTraceOptionsFactory;
import com.google.cloud.trace.core.SpanContextFactory;
import com.google.cloud.trace.core.TraceOptionsFactory;
import com.google.cloud.trace.grpc.v1.GrpcTraceConsumer;
import com.google.cloud.trace.sink.TraceSink;
import com.google.cloud.trace.v1.TraceSinkV1;
import com.google.cloud.trace.v1.consumer.ScheduledBufferingTraceConsumer;
import com.google.cloud.trace.v1.consumer.TraceConsumer;
import com.google.cloud.trace.v1.producer.TraceProducer;
import com.google.cloud.trace.v1.util.RoughTraceSizer;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Provides a gRPC Trace Service. The provided {@link Tracer} will write Traces to the Stackdriver
 * Trace gRPC API using the application default credentials.
 */
public class TraceGrpcApiService implements TraceService {
  public static class Builder {
    private String projectId;
    private TraceOptionsFactory optionsFactory = new RateLimitingTraceOptionsFactory(1.0, false);
    private int bufferSize = 32 * 1024;
    private int scheduledDelay = 15;
    private GoogleCredentials credentials;

    private Builder() {}

    /**
     * Sets the projectId.
     * @param projectId The GCP project the traces should be associated with.
     */
    public Builder setProjectId(String projectId) {
      if (projectId == null || projectId.isEmpty()) {
        throw new IllegalArgumentException("Project id must not be empty.");
      }
      this.projectId = projectId;
      return this;
    }

    /**
     * Sets the {@link TraceOptionsFactory}.
     * @param optionsFactory The {@link TraceOptionsFactory} which will be used for id generation
     * and sampling decisions. Optional. Defaults to sampling at 1 trace/second.
     */
    public Builder setTraceOptionsFactory(TraceOptionsFactory optionsFactory) {
      if (optionsFactory == null) {
        throw new IllegalArgumentException("TraceOptionsFactory must not be null.");
      }
      this.optionsFactory = optionsFactory;
      return this;
    }

    /**
     * Sets the buffer size.
     * @param bufferSize The maximum local buffer size (in bytes) to use before flushing to the
     * Stackdriver Trace API. Optional. Defaults to 32kb.
     */
    public Builder setBufferSize(int bufferSize) {
      if (bufferSize < 0) {
        throw new IllegalArgumentException("Buffer size must be >= 0.");
      }
      this.bufferSize = bufferSize;
      return this;
    }

    /**
     * Sets the scheduled delay for writing traces. Optional. Defaults to 15 seconds.
     * @param scheduledDelay The maximum number of seconds a Trace will be buffered locally before
     * being written to the Stackdriver Trace API.
     */
    public Builder setScheduledDelay(int scheduledDelay) {
      this.scheduledDelay = scheduledDelay;
      return this;
    }

    /**
     * Sets the credentials to be used for the Stackdriver Trace API call. Optional. By default, the
     * application default credentials will be used.
     * @param credentials The credentials to use for calls to the Stackdriver Trace API.
     */
    public Builder setCredentials(GoogleCredentials credentials) {
      this.credentials = credentials;
      return this;
    }

    /**
     * Builds a new TraceGrpcApiService.
     */
    public TraceGrpcApiService build() throws IOException {
      if (credentials == null) {
        credentials = GoogleCredentials.getApplicationDefault();
      }
      return new TraceGrpcApiService(projectId, optionsFactory, bufferSize,
          scheduledDelay, credentials);
    }
  }

  /**
   * Returns a new builder.
   * @return the new builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  private final Tracer tracer;
  private final SpanContextHandler handler;
  private final SpanContextFactory factory;

  private TraceGrpcApiService(String projectId, TraceOptionsFactory optionsFactory,
      int bufferSize, int scheduledDelay, GoogleCredentials credentials) throws IOException {
    TraceProducer traceProducer = new TraceProducer();
    TraceConsumer traceConsumer = new GrpcTraceConsumer("cloudtrace.googleapis.com",
        credentials);
    ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);
    // Have the flushing threads shutdown if idle for the scheduled delay.
    executorService.allowCoreThreadTimeOut(true);
    executorService.setKeepAliveTime(scheduledDelay, TimeUnit.SECONDS);
    traceConsumer = new ScheduledBufferingTraceConsumer(traceConsumer, new RoughTraceSizer(),
        bufferSize, scheduledDelay, executorService);
    TraceSink traceSink = new TraceSinkV1(projectId, traceProducer, traceConsumer);

    factory = new SpanContextFactory(optionsFactory);
    handler = new GrpcSpanContextHandler(factory.initialContext());
    tracer = new SpanContextHandlerTracer(traceSink, handler, factory, new JavaTimestampFactory());
  }

  @Override
  public Tracer getTracer() {
    return tracer;
  }

  @Override
  public SpanContextHandler getSpanContextHandler() {
    return handler;
  }

  @Override
  public SpanContextFactory getSpanContextFactory() {
    return factory;
  }
}
