package com.google.cloud.trace.v1.sink;

import com.google.devtools.cloudtrace.v1.Trace;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingTraceSink implements TraceSink {
  private final Logger logger;
  private final Level level;

  public LoggingTraceSink(Logger logger, Level level) {
    this.logger = logger;
    this.level = level;
  }

  @Override
  public void receive(Trace trace) {
    logger.log(level, "Received trace:\n{0}", trace);
  }
}
