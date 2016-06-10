package com.google.cloud.trace.guice.auth;

import com.google.inject.AbstractModule;

public class ClientSecretsFilePropertiesModule extends AbstractModule {
  @Override
  protected void configure() {
    String clientSecretsFile = System.getProperty("cloudtrace.client-secrets-file");
    bind(String.class).annotatedWith(ClientSecretsFile.class).toInstance(clientSecretsFile);
  }
}
