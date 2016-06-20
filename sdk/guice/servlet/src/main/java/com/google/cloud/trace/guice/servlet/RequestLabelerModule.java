package com.google.cloud.trace.guice.servlet;

import com.google.cloud.trace.guice.annotation.Labeler;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;

public class RequestLabelerModule extends AbstractModule {
  @Override
  protected void configure() {
    MapBinder<String, Labeler> mapBinder =
        MapBinder.newMapBinder(binder(), String.class, Labeler.class);
    mapBinder.addBinding(RequestLabeler.KEY).to(RequestLabeler.class).in(Singleton.class);
  }
}
