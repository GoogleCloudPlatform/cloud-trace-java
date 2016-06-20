package com.google.cloud.trace.guice.v1;

import com.google.inject.AbstractModule;

public class ProjectIdPropertiesModule extends AbstractModule {
  @Override
  protected void configure() {
    String projectId = System.getProperty("cloudtrace.project-id");
    bind(String.class).annotatedWith(ProjectId.class).toInstance(projectId);
  }
}
