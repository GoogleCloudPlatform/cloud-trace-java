package com.google.cloud.trace.guice;

import com.google.cloud.trace.util.ConstantTraceOptionsFactory;
import com.google.cloud.trace.util.TraceOptionsFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.Set;

public class ConstantTraceOptionsFactoryModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  TraceOptionsFactory provideTraceOptionsFactory(
      @TraceEnabled boolean traceEnabled, @StackTraceEnabled boolean stackTraceEnabled) {
    return new ConstantTraceOptionsFactory(traceEnabled, stackTraceEnabled);
  }
}
