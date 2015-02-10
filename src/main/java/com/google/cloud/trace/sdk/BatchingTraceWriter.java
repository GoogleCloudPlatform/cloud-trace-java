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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writer implementation that wraps another writer, batches and uses the wrapped
 * writer to write traces when the batch size is met.
 */
public class BatchingTraceWriter implements TraceWriter, CanInitFromProperties {

  private static final Logger logger = Logger.getLogger(BatchingTraceWriter.class.getName());

  public static final int DEFAULT_BATCH_SIZE = 20;
  
  private LinkedBlockingQueue<TraceSpanData> batch = new LinkedBlockingQueue<>();
  private TraceWriter writer = new LoggingTraceWriter();
  private int batchSize = DEFAULT_BATCH_SIZE;
  private ExecutorService executor = Executors.newFixedThreadPool(5);
  
  public BatchingTraceWriter(){
  }
  
  public BatchingTraceWriter(int batchSize, TraceWriter writer) {
    this.batchSize = batchSize;
    this.writer = writer;
  }

  /**
   * Initializes the writer from a properties file.
   */
  @Override
  public void initFromProperties(Properties props) {
    String batchSizeStr = props.getProperty(getClass().getName() + ".batchSize");
    if (batchSizeStr != null) {
      try {
        this.batchSize = Integer.parseInt(batchSizeStr);
      } catch (NumberFormatException nfe) {
        logger.log(Level.WARNING,
            "Found non-numeric batch size in the properties (" + batchSizeStr + ")");
      }
    }
    
    String innerWriterClassName = props.getProperty(getClass().getName() + ".traceWriter");
    if (innerWriterClassName != null && !innerWriterClassName.isEmpty()) {
      this.writer =
          (TraceWriter) ReflectionUtils.createFromProperties(innerWriterClassName, props);
    }
  }

  public void setWriter(TraceWriter writer) {
    this.writer = writer;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public TraceWriter getInnerWriter() {
    return writer;
  }
  
  public void setExecutor(ExecutorService executor) {
    if (this.executor != null) {
      this.executor.shutdown();
    }
    this.executor = executor;
  }
  
  /**
   * Adds a single span to the batch and writes out the batch if the
   * batch size is met.
   */
  @Override
  public void writeSpan(TraceSpanData span) {
    batch.add(span);
    checkWriteBatch();
  }

  /**
   * Adds a list of spans to the batch and writes out the batch if the
   * batch size is met. Note that it's possible that we will pass a batch
   * larger than the batch size to the inner write if appending this list
   * causes the batch to exceed that size.
   */
  @Override
  public void writeSpans(List<TraceSpanData> spans) {
    batch.addAll(spans);
    checkWriteBatch();
  }

  public void setExecutorService(ExecutorService executor) {
    this.executor = executor;
  }
  
  private void checkWriteBatch() {
    if (batch.size() >= batchSize) {
      final List<TraceSpanData> writeBatch = drainBatch();
      executor.execute(new Runnable() {
        @Override
        public void run() {
          try {
            writer.writeSpans(writeBatch);
          } catch (CloudTraceException e) {
            logger.log(Level.SEVERE, "Exception writing trace batch", e);
          }
        }
      });
    }
  }

  private List<TraceSpanData> drainBatch() {
    List<TraceSpanData> writeBatch = new ArrayList<>();
    batch.drainTo(writeBatch);
    return writeBatch;
  }

  @Override
  public void shutdown() throws CloudTraceException {
    writer.writeSpans(drainBatch());
  }
}
