package com.google.cloud.trace.guice.v1;

import com.google.inject.AbstractModule;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SinkScheduledDelayJndiModule extends AbstractModule {
  @Override
  protected void configure() {
    Integer sinkScheduledDelay;
    try {
      Context env = (Context) new InitialContext().lookup("java:comp/env");
      sinkScheduledDelay = (Integer) env.lookup("sinkScheduledDelay");
    } catch (NamingException ex) {
      throw new RuntimeException(ex);
    }
    bind(int.class).annotatedWith(SinkScheduledDelay.class).toInstance(sinkScheduledDelay);
  }
}
