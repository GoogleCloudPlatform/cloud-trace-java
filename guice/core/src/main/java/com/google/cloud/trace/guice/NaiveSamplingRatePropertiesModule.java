package com.google.cloud.trace.guice;

import com.google.inject.AbstractModule;

public class NaiveSamplingRatePropertiesModule extends AbstractModule {
  @Override
  protected void configure() {
    String naiveSamplingRate = System.getProperty("cloudtrace.naive-sampling-rate");
    bind(double.class).annotatedWith(NaiveSamplingRate.class)
        .toInstance(Double.parseDouble(naiveSamplingRate));
  }
}
