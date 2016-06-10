package com.google.cloud.trace.guice.v1;

import com.google.inject.AbstractModule;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ProjectIdJndiModule extends AbstractModule {
  @Override
  protected void configure() {
    String projectId;
    try {
      Context env = (Context) new InitialContext().lookup("java:comp/env");
      projectId = (String) env.lookup("projectId");
    } catch (NamingException ex) {
      throw new RuntimeException(ex);
    }
    bind(String.class).annotatedWith(ProjectId.class).toInstance(projectId);
  }
}
