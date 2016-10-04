package com.google.cloud.trace.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import com.google.cloud.trace.util.JodaTimestampFactory;
import com.google.cloud.trace.util.TimestampFactory;

public class JodaTimestampFactoryModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TimestampFactory.class).to(JodaTimestampFactory.class).in(Singleton.class);
  }
}
