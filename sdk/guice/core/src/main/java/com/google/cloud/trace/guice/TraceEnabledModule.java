package com.google.cloud.trace.guice;

import com.google.inject.AbstractModule;

public class TraceEnabledModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(boolean.class).annotatedWith(TraceEnabled.class).toInstance(true);
  }
}
