package com.google.cloud.trace.guice.annotation;

import com.google.cloud.trace.ManagedTracer;
import com.google.cloud.trace.annotation.Span;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.MapBinder;

import java.util.Map;

public class ManagedTracerSpanModule extends AbstractModule {
  private final String labelHost;

  public ManagedTracerSpanModule() {
    this("");
  }

  public ManagedTracerSpanModule(String labelHost) {
    this.labelHost = labelHost;
  }

  @Override
  protected void configure() {
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(Span.class),
        new ManagedTracerSpanInterceptor(getProvider(ManagedTracer.class),
            getProvider(Key.get(new TypeLiteral<Map<String, Labeler>>(){})), labelHost));
    MapBinder<String, Labeler> mapBinder =
        MapBinder.newMapBinder(binder(), String.class, Labeler.class);
  }
}
