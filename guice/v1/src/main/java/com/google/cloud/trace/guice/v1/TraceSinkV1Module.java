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

package com.google.cloud.trace.guice.v1;

import com.google.cloud.trace.sink.TraceSink;
import com.google.cloud.trace.v1.TraceSinkV1;
import com.google.cloud.trace.v1.consumer.TraceConsumer;
import com.google.cloud.trace.v1.producer.TraceProducer;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public class TraceSinkV1Module extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<TraceSink> setBinder = Multibinder.newSetBinder(binder(), TraceSink.class);
    setBinder.addBinding().toProvider(TraceSinkV1Provider.class).in(Singleton.class);
  }

  private static class TraceSinkV1Provider implements Provider<TraceSinkV1> {
    private final String projectId;
    private final TraceConsumer traceConsumer;

    @Inject
    TraceSinkV1Provider(@ProjectId String projectId, TraceConsumer traceConsumer) {
      this.projectId = projectId;
      this.traceConsumer = traceConsumer;
    }

    @Override
    public TraceSinkV1 get() {
      return new TraceSinkV1(projectId, new TraceProducer(), traceConsumer);
    }
  }
}
