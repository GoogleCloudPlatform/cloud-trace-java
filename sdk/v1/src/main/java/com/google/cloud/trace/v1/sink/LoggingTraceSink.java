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

package com.google.cloud.trace.v1.sink;

import com.google.devtools.cloudtrace.v1.Trace;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A trace sink that logs the trace messages that it receives.
 *
 * @see Level
 * @see Logger
 * @see Trace
 * @see TraceSink
 */
public class LoggingTraceSink implements TraceSink {
  private final Logger logger;
  private final Level level;

  /**
   * Creates a logging trace sink.
   *
   * @param logger a logger that logs trace messages.
   * @param level  the level to log trace messages.
   */
  public LoggingTraceSink(Logger logger, Level level) {
    this.logger = logger;
    this.level = level;
  }

  @Override
  public void receive(Trace trace) {
    logger.log(level, "Received trace:\n{0}", trace);
  }
}
