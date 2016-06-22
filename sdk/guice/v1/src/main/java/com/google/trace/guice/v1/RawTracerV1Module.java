package com.google.cloud.trace.guice.v1;

import com.google.cloud.trace.RawTracer;
import com.google.cloud.trace.v1.RawTracerV1;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.cloud.trace.v1.source.TraceSource;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public class RawTracerV1Module extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<RawTracer> setBinder = Multibinder.newSetBinder(binder(), RawTracer.class);
    setBinder.addBinding().toProvider(RawTracerV1Provider.class).in(Singleton.class);
  }

  private static class RawTracerV1Provider implements Provider<RawTracerV1> {
    private final String projectId;
    private final TraceSink traceSink;

    @Inject
    RawTracerV1Provider(@ProjectId String projectId, TraceSink traceSink) {
      this.projectId = projectId;
      this.traceSink = traceSink;
    }

    @Override
    public RawTracerV1 get() {
      return new RawTracerV1(projectId, new TraceSource(), traceSink);
    }
  }
}
