package com.google.cloud.trace;

import com.google.cloud.trace.util.TraceContext;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingTraceContextHandler extends AbstractTraceContextHandler {
  private final Logger logger;
  private final Level level;

  public LoggingTraceContextHandler(TraceContext context, Logger logger, Level level) {
    super(context);
    this.logger = logger;
    this.level = level;
    logger.log(level, "Initialized. Current:\n{0}", context);
  }

  @Override
  public void doPush(TraceContext context) {
    logger.log(level, "Pushed context. Current:\n{0}", context);
  }

  @Override
  public void doPop(TraceContext context) {
    logger.log(level, "Popped context. Current:\n{0}", context);
  }
}
