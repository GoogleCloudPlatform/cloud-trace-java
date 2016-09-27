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

package com.google.cloud.trace.guice.grpc.v1;

import com.google.auth.Credentials;
import com.google.cloud.trace.grpc.v1.GrpcTraceSink;
import com.google.cloud.trace.guice.api.ApiHost;
import com.google.cloud.trace.guice.v1.ApiTraceSink;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class GrpcTraceSinkModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @ApiTraceSink
  @Singleton
  TraceSink provideTraceSink(@ApiHost String apiHost, Credentials credentials) {
    return new GrpcTraceSink(apiHost, credentials);
  }
}
