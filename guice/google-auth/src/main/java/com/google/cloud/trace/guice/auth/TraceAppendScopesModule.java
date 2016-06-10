package com.google.cloud.trace.guice.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.Arrays;
import java.util.List;

public class TraceAppendScopesModule extends AbstractModule {
  @Override
  protected void configure() {
  }

  @Provides
  @Scopes
  @Singleton
  List<String> provideScopes() {
    return Arrays.asList("https://www.googleapis.com/auth/trace.append");
  }
}
