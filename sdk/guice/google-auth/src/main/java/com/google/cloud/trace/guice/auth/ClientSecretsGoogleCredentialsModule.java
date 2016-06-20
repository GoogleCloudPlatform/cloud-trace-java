package com.google.cloud.trace.guice.auth;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ClientSecretsGoogleCredentialsModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  Credentials provideCredentials(@ClientSecretsFile String clientSecretsFile,
      @Scopes List<String> scopes) throws FileNotFoundException, IOException {
    return GoogleCredentials
        .fromStream(new FileInputStream(clientSecretsFile))
        .createScoped(scopes);
  }
}
