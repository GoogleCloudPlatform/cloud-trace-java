package com.google.cloud.trace.guice.v1;

import com.google.cloud.trace.v1.sink.FlushableTraceSink;
import com.google.cloud.trace.v1.sink.SimpleBufferingTraceSink;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class SimpleBufferingTraceSinkModule extends AbstractModule {
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
  FlushableTraceSink provideFlushableTraceSink(@ApiTraceSink TraceSink traceSink) {
    return new SimpleBufferingTraceSink(traceSink);
  }
}
