// Copyright 2016 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace;

import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.SpanKind;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.Timestamp;
import com.google.cloud.trace.core.TraceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A raw tracer that logs trace events.
 */
public class LoggingTraceSink implements TraceSink {
  private final Logger logger;
  private final Level level;

  /**
   * Creates a new raw tracer.
   *
   * @param logger a logger used to log trace events.
   * @param level a level used for trace event log messages.
   */
  public LoggingTraceSink(Logger logger, Level level) {
    this.logger = logger;
    this.level = level;
  }

  @Override
  public void startSpan(TraceContext context, TraceContext parentContext, SpanKind spanKind,
      String name, Timestamp timestamp) {
    logger.log(level, String.format("LoggingTraceSink.startSpan(%s, %s, %s, %s, %s)",
        context, parentContext, spanKind, name, timestamp));
  }

  @Override
  public void endSpan(TraceContext context, Timestamp timestamp) {
    logger.log(level, String.format("LoggingTraceSink.endSpan(%s, %s)", context, timestamp));
  }

  @Override
  public void annotateSpan(TraceContext context, Labels labels) {
    logger.log(level, String.format("LoggingTraceSink.annotateSpan(%s, %s)", context, labels));
  }

  @Override
  public void setStackTrace(TraceContext context, StackTrace stackTrace) {
    logger.log(level, String.format("LoggingTraceSink.setStackTrace(%s, %s)", context, stackTrace));
  }
}
