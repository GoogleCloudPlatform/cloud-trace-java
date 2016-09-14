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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.api.client.http.HttpTransport;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.trace.guice.auth.Scopes;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import java.io.IOException;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Singleton;

/**
 * Provides {@link Credentials} for using the Stackdriver Trace API.
 *
 * <p>This class is unconditionally thread-safe.
 */
@ThreadSafe
public final class DefaultCredentialsModule extends AbstractModule {

  @Override
  protected final void configure() {
    requireBinding(HttpTransport.class);
    requireBinding(Key.get(new TypeLiteral<List<String>>() {}, Scopes.class));
  }

  @Provides
  @Singleton
  final Credentials provideCredentials(HttpTransport transport, @Scopes List<String> scopes)
      throws IOException {
    return GoogleCredentials.getApplicationDefault(checkNotNull(transport)).createScoped(scopes);
  }
}
