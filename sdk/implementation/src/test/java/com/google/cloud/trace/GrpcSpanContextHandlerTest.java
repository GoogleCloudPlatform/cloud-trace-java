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

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanContextHandle;
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import java.math.BigInteger;
import org.junit.Test;

public class GrpcSpanContextHandlerTest {
  private static final SpanContext first = new SpanContext(new TraceId(BigInteger.valueOf(1)), new SpanId(1), new TraceOptions(1));
  private static final SpanContext second = new SpanContext(new TraceId(BigInteger.valueOf(2)), new SpanId(2), new TraceOptions(2));
  private static final SpanContext third = new SpanContext(new TraceId(BigInteger.valueOf(3)), new SpanId(3), new TraceOptions(3));

  @Test
  public void testAttachDetach() throws Exception {
    SpanContextHandler contextHandler = new GrpcSpanContextHandler(first);
    assertThat(contextHandler.current()).isEqualTo(first);
    SpanContextHandle handle1 = contextHandler.attach(second);
    assertThat(contextHandler.current()).isEqualTo(second);
    SpanContextHandle handle2 = contextHandler.attach(third);
    assertThat(contextHandler.current()).isEqualTo(third);
    handle2.detach();
    assertThat(contextHandler.current()).isEqualTo(second);
    handle1.detach();
    assertThat(contextHandler.current()).isEqualTo(first);
  }
}
