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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * Tests for the {@link BatchingTraceWriter} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class BatchingTraceWriterTest {

  private BatchingTraceWriter writer;
  @Mock private TraceWriter mockWriter;
  @Mock private ExecutorService mockExecutor;
  
  @Before
  public void setUp() {
    writer = new BatchingTraceWriter(2, mockWriter);
    writer.setExecutor(mockExecutor);
  }
  
  @Test
  public void testWriteSpan() throws CloudTraceException {
    TraceSpanData span1 = createDummyTraceSpanData();
    TraceSpanData span2 = createDummyTraceSpanData();
    List<TraceSpanData> expectedBatch = new ArrayList<>();
    expectedBatch.add(span1);
    expectedBatch.add(span2);

    // Submit the first one, below the batch size.
    writer.writeSpan(span1);
    Mockito.verifyNoMoreInteractions(mockWriter, mockExecutor);
    
    // Hit the batch size.
    ArgumentCaptor<Runnable> writeJob = ArgumentCaptor.forClass(Runnable.class);
    writer.writeSpan(span2);
    Mockito.verify(mockExecutor).execute(writeJob.capture());
    writeJob.getValue().run();
    Mockito.verify(mockWriter).writeSpans(Matchers.eq(expectedBatch));
    
    // Add another one, we should be batching them up again.
    writer.writeSpan(span1);
    Mockito.verifyNoMoreInteractions(mockWriter, mockExecutor);
  }

  @Test
  public void testInitFromProperties() {
    writer = new BatchingTraceWriter();
    Properties props = new Properties();
    props.setProperty(BatchingTraceWriter.class.getName() + ".batchSize", "19");
    props.setProperty(BatchingTraceWriter.class.getName() + ".traceWriter",
        LoggingTraceWriter.class.getName());
    writer.initFromProperties(props);
    assertEquals(19, writer.getBatchSize());
    assertTrue(writer.getInnerWriter() instanceof LoggingTraceWriter);
  }

  private TraceSpanData createDummyTraceSpanData() {
    return new TraceSpanData(null, null, null, 0, true);
  }
}
