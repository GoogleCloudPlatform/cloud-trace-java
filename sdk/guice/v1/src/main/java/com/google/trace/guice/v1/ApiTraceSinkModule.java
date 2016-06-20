package com.google.cloud.trace.guice.v1;

import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class ApiTraceSinkModule extends AbstractModule {
  @Override
  protected void configure() {
  }

  @Provides
  @Singleton
  TraceSink provideTraceSink(@ApiTraceSink TraceSink traceSink) {
    return traceSink;
  }
}
