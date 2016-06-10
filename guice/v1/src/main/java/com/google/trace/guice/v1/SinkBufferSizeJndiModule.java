package com.google.cloud.trace.guice.v1;

import com.google.inject.AbstractModule;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SinkBufferSizeJndiModule extends AbstractModule {
  @Override
  protected void configure() {
    Integer sinkBufferSize;
    try {
      Context env = (Context) new InitialContext().lookup("java:comp/env");
      sinkBufferSize = (Integer) env.lookup("sinkBufferSize");
    } catch (NamingException ex) {
      throw new RuntimeException(ex);
    }
    bind(int.class).annotatedWith(SinkBufferSize.class).toInstance(sinkBufferSize);
  }
}
