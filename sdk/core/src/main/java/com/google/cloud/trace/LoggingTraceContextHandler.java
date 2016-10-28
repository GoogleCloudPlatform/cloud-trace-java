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

import com.google.cloud.trace.core.TraceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A trace context handler that logs trace contexts as they are pushed on and popped off the stack.
 */
public class LoggingTraceContextHandler extends AbstractTraceContextHandler {
  private final Logger logger;
  private final Level level;

  /**
   * Creates a new trace context handler.
   *
   * @param context a trace context that serves as the root trace context.
   * @param logger a logger used to log trace contexts.
   * @param level a level used for trace context log messages.
   */
  public LoggingTraceContextHandler(TraceContext context, Logger logger, Level level) {
    super(context);
    this.logger = logger;
    this.level = level;
    logger.log(level, "Initialized. Current:\n{0}", context);
  }

  /**
   * Logs the new trace context pushed onto the stack.
   */
  @Override
  public void doPush(TraceContext context) {
    logger.log(level, "Pushed context. Current:\n{0}", context);
  }

  /**
   * Logs the trace context popped off the stack.
   */
  @Override
  public void doPop(TraceContext context) {
    logger.log(level, "Popped context. Current:\n{0}", context);
  }
}
