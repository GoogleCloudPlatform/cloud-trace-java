package com.google.cloud.trace.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import com.google.cloud.trace.util.JavaTimestampFactory;
import com.google.cloud.trace.util.TimestampFactory;

public class JavaTimestampFactoryModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TimestampFactory.class).to(JavaTimestampFactory.class).in(Singleton.class);
  }
}
