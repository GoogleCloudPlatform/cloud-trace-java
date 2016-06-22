package com.google.cloud.trace.guice;

import com.google.inject.AbstractModule;

public class StackTraceEnabledModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(boolean.class).annotatedWith(StackTraceEnabled.class).toInstance(true);
  }
}
