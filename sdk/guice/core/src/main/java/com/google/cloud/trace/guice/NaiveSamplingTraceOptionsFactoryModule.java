package com.google.cloud.trace.guice;

import com.google.cloud.trace.util.NaiveSamplingTraceOptionsFactory;
import com.google.cloud.trace.util.TraceOptionsFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.Set;

public class NaiveSamplingTraceOptionsFactoryModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  TraceOptionsFactory provideTraceOptionsFactory(
      @NaiveSamplingRate double samplingRate, @StackTraceEnabled boolean stackTraceEnabled) {
    return new NaiveSamplingTraceOptionsFactory(samplingRate, stackTraceEnabled);
  }
}
