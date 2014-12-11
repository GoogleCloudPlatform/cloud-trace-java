// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.sdk;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writer implementation that writes trace information to a standard log file.
 * Writes at the INFO level. The logger can be configured using standard Java
 * logging approaches.
 */
public class LoggingTraceWriter implements TraceWriter {

  private static final Logger logger = Logger.getLogger(LoggingTraceWriter.class.getName());
  
  @Override
  public void writeSpan(TraceSpanData span) {
    logger.log(Level.INFO, span.toString());
  }

  @Override
  public void writeSpans(List<TraceSpanData> spans) {
    for (TraceSpanData span : spans) {
      writeSpan(span);
    }
  }

  @Override
  public void shutdown() {
  }
}
