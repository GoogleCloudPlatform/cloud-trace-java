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

package com.google.cloud.trace.v1.consumer;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.cloud.trace.v1.util.Sizer;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.Traces;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class SizedBufferingTraceConsumerTest {

  @Test
  public void testBuffering() {
    ArgumentCaptor<Traces> tracesCaptor = ArgumentCaptor.forClass(Traces.class);
    TraceConsumer mockSink = mock(TraceConsumer.class);
    final Trace trace1 = Trace.newBuilder().setProjectId("1").setTraceId("1").build();
    final Trace trace2 = Trace.newBuilder().setProjectId("1").setTraceId("2").build();
    final Trace trace3 = Trace.newBuilder().setProjectId("1").setTraceId("3").build();
    Sizer<Trace> testSizer = new Sizer<Trace>() {

      @Override
      public int size(Trace sizeable) {
        if (sizeable == trace1) {
          return 10;
        } else if (sizeable == trace2) {
          return 20;
        } else if (sizeable == trace3) {
          return 30;
        } else {
          return 0;
        }
      }
    };

    FlushableTraceConsumer bufferedSink = new SizedBufferingTraceConsumer(mockSink, testSizer, 25);
    bufferedSink.receive(Traces.newBuilder().addTraces(trace1).build()); // buffer size: 10
    bufferedSink.receive(Traces.newBuilder().addTraces(trace2).build()); // buffer size: 30
    // should flush

    bufferedSink.receive(Traces.newBuilder().addTraces(trace3).build()); // buffer size: 30
    // should flush

    verify(mockSink, times(2)).receive(tracesCaptor.capture());
    List<Traces> traceBatches = tracesCaptor.getAllValues();

    assertThat(traceBatches).hasSize(2);
    assertThat(traceBatches.get(0).getTracesList()).containsExactly(trace1, trace2);
    assertThat(traceBatches.get(1).getTracesList()).containsExactly(trace3);
  }
}
