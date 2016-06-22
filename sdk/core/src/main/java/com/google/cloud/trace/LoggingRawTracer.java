package com.google.cloud.trace;

import com.google.cloud.trace.util.Labels;
import com.google.cloud.trace.util.SpanKind;
import com.google.cloud.trace.util.StackTrace;
import com.google.cloud.trace.util.Timestamp;
import com.google.cloud.trace.util.TraceContext;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingRawTracer implements RawTracer {
  private final Logger logger;
  private final Level level;

  public LoggingRawTracer(Logger logger, Level level) {
    this.logger = logger;
    this.level = level;
  }

  @Override
  public void startSpan(TraceContext context, TraceContext parentContext, SpanKind spanKind,
      String name, Timestamp timestamp) {
    logger.log(level, String.format("LoggingRawTracer.startSpan(%s, %s, %s, %s, %s)",
        context, parentContext, spanKind, name, timestamp));
  }

  @Override
  public void endSpan(TraceContext context, Timestamp timestamp) {
    logger.log(level, String.format("LoggingRawTracer.endSpan(%s, %s)", context, timestamp));
  }

  @Override
  public void annotateSpan(TraceContext context, Labels labels) {
    logger.log(level, String.format("LoggingRawTracer.annotateSpan(%s, %s)", context, labels));
  }

  @Override
  public void setStackTrace(TraceContext context, StackTrace stackTrace) {
    logger.log(level, String.format("LoggingRawTracer.setStackTrace(%s, %s)", context, stackTrace));
  }
}
