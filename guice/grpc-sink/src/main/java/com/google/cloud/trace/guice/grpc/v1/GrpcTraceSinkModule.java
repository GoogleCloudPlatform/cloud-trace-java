package com.google.cloud.trace.guice.grpc.v1;

import com.google.auth.Credentials;
import com.google.cloud.trace.grpc.v1.GrpcTraceSink;
import com.google.cloud.trace.guice.api.ApiHost;
import com.google.cloud.trace.guice.v1.ApiTraceSink;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.concurrent.Executors;

public class GrpcTraceSinkModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @ApiTraceSink
  @Singleton
  TraceSink provideTraceSink(@ApiHost String apiHost, Credentials credentials) {
    // The executor does not yet appear to be used by the gRPC client auth interceptor.
    return new GrpcTraceSink(apiHost, credentials, Executors.newSingleThreadExecutor());
  }
}
