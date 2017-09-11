package com.google.cloud.trace.samples.guice.servlet;

import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.LoggingEnhancer;
import com.google.cloud.trace.SpanContextHandler;
import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.TraceId;

/**
 * Logging enhancer to set the LogEntry.trace property based on the current trace context in the
 * format "projects/[PROJECT-ID]/traces/[TRACE-ID]".
 *
 * This allows log entries to be displayed inline with trace spans in the Stackdriver Trace Viewer.
 */
public class SpanContextLoggingEnhancer implements LoggingEnhancer {

  private String tracePrefix;
  private SpanContextHandler spanContextHandler;

  public SpanContextLoggingEnhancer(SpanContextHandler spanContextHandler, String projectId) {
    this.tracePrefix = "projects/" + projectId + "/traces/";
    this.spanContextHandler = spanContextHandler;
  }

  @Override
  public void enhanceLogEntry(LogEntry.Builder builder) {
    SpanContext spanContext = spanContextHandler.current();
    if (spanContext != null) {
      TraceId traceId = spanContext.getTraceId();
      if (traceId != null) {
        builder.setTrace(tracePrefix + traceId.getApiString());
      }
    }
  }
}
