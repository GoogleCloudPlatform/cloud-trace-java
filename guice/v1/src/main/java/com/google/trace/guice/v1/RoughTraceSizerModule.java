package com.google.cloud.trace.guice.v1;

import com.google.cloud.trace.v1.util.RoughTraceSizer;
import com.google.cloud.trace.v1.util.Sizer;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.Singleton;

public class RoughTraceSizerModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(new TypeLiteral<Sizer<Trace>>(){}).to(RoughTraceSizer.class).in(Singleton.class);
  }
}
