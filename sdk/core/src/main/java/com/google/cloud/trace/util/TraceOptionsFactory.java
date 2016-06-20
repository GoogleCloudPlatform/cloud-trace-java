package com.google.cloud.trace.util;

public interface TraceOptionsFactory {
  TraceOptions create();
  TraceOptions create(TraceOptions parent);
}
