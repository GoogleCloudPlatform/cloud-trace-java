// Copyright 2016 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
