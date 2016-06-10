package com.google.cloud.trace.guice.v1;

import com.google.cloud.trace.v1.sink.FlushableTraceSink;
import com.google.cloud.trace.v1.sink.ScheduledBufferingTraceSink;
import com.google.cloud.trace.v1.sink.TraceSink;
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
  TraceSink provideTraceSink(FlushableTraceSink flushableTraceSink) {
    return flushableTraceSink;
  }

  @Provides
  @Singleton
  FlushableTraceSink provideFlushableTraceSink(
      @ApiTraceSink TraceSink traceSink, Sizer<Trace> traceSizer, @SinkBufferSize int bufferSize,
      @SinkScheduledDelay int scheduledDelay, ScheduledExecutorService scheduler) {
    return new ScheduledBufferingTraceSink(
        traceSink, traceSizer, bufferSize, scheduledDelay, scheduler);
  }
}
