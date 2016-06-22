package com.google.cloud.trace.guice.v1;

import com.google.inject.AbstractModule;

public class SinkBufferSizePropertiesModule extends AbstractModule {
  @Override
  protected void configure() {
    String sinkBufferSizeString = System.getProperty("cloudtrace.sink-buffer-size", "65536");
    int sinkBufferSize = Integer.valueOf(sinkBufferSizeString);
    bind(int.class).annotatedWith(SinkBufferSize.class).toInstance(sinkBufferSize);
  }
}
