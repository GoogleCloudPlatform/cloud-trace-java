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

import com.google.cloud.trace.v1.consumer.FlushableTraceConsumer;
import com.google.cloud.trace.v1.consumer.ScheduledBufferingTraceConsumer;
import com.google.cloud.trace.v1.consumer.TraceConsumer;
import com.google.cloud.trace.v1.util.Sizer;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.concurrent.ScheduledExecutorService;

public class ScheduledBufferingTraceSinkModule extends AbstractModule {
  @Override
  protected void configure() {
  }

  @Provides
  @Singleton
  TraceConsumer provideTraceSink(FlushableTraceConsumer flushableTraceSink) {
    return flushableTraceSink;
  }

  @Provides
  @Singleton
  FlushableTraceConsumer provideFlushableTraceSink(
      @ApiTraceSink TraceConsumer traceConsumer, Sizer<Trace> traceSizer, @SinkBufferSize int bufferSize,
      @SinkScheduledDelay int scheduledDelay, ScheduledExecutorService scheduler) {
    return new ScheduledBufferingTraceConsumer(
        traceConsumer, traceSizer, bufferSize, scheduledDelay, scheduler);
  }
}
