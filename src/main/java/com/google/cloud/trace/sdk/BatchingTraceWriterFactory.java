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

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory class for {@link BatchingTraceWriter}s.
 */
public class BatchingTraceWriterFactory implements TraceWriterFactory {

  private static final Logger logger = Logger.getLogger(BatchingTraceWriterFactory.class.getName());

  @Override
  public BatchingTraceWriter createTraceWriter(Properties props) {
    int batchSize = BatchingTraceWriter.DEFAULT_BATCH_SIZE;
    TraceWriter innerWriter = new LoggingTraceWriter();
    String batchSizeStr = props.getProperty(getClass().getName() + ".batchSize");
    if (batchSizeStr != null) {
      try {
        batchSize = Integer.parseInt(batchSizeStr);
      } catch (NumberFormatException nfe) {
        logger.log(Level.WARNING,
            "Found non-numeric batch size in the properties (" + batchSizeStr + ")");
      }
    }
    
    String innerWriterFactoryName = props.getProperty(getClass().getName() + ".traceWriterFactory");
    if (innerWriterFactoryName != null && innerWriterFactoryName.length() > 0) {
      TraceWriterFactory factory =
          TraceWriterFactoryHelper.createTraceWriterFactory(innerWriterFactoryName);
      if (factory != null) {
        innerWriter = factory.createTraceWriter(props);
      }
    }

    return new BatchingTraceWriter(batchSize, innerWriter);
  }
}
