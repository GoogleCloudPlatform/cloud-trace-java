package com.google.cloud.trace.guice.v1;

import com.google.inject.AbstractModule;

public class SinkScheduledDelayPropertiesModule extends AbstractModule {
  @Override
  protected void configure() {
    String sinkScheduledDelayString = System.getProperty("cloudtrace.sink-scheduled-delay", "10");
    int sinkScheduledDelay = Integer.valueOf(sinkScheduledDelayString);
    bind(int.class).annotatedWith(SinkScheduledDelay.class).toInstance(sinkScheduledDelay);
  }
}
