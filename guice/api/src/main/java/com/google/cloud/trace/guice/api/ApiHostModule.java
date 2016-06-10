package com.google.cloud.trace.guice.api;

import com.google.inject.AbstractModule;

public class ApiHostModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(String.class).annotatedWith(ApiHost.class).toInstance("cloudtrace.googleapis.com");
  }
}
