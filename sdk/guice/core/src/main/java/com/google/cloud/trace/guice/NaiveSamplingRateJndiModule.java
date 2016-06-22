package com.google.cloud.trace.guice;

import com.google.inject.AbstractModule;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class NaiveSamplingRateJndiModule extends AbstractModule {
  @Override
  protected void configure() {
    Double samplingRate;
    try {
      Context env = (Context) new InitialContext().lookup("java:comp/env");
      samplingRate = (Double) env.lookup("naiveSamplingRate");
    } catch (NamingException ex) {
      throw new RuntimeException(ex);
    }
    bind(double.class).annotatedWith(NaiveSamplingRate.class).toInstance(samplingRate);
  }
}
