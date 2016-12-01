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
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import java.math.BigInteger;
import org.junit.Test;

public class TestSpanContextHandlerTest {
  private static final SpanContext context = new SpanContext(
      new TraceId(BigInteger.valueOf(7654)),
      new SpanId(3210),
      new TraceOptions(3));

  @Test
  public void testTestSpanContextHandler() {
    TestSpanContextHandler handler1 = new TestSpanContextHandler();
    assertThat(handler1.current()).isEqualTo(TestSpanContextHandler.TEST_CONTEXT);

    TestSpanContextHandler handler2 = new TestSpanContextHandler(
        TestSpanContextHandler.TEST_CONTEXT);
    assertThat(handler2.current()).isEqualTo(TestSpanContextHandler.TEST_CONTEXT);
  }

  @Test
  public void testCurrent() {
    TestSpanContextHandler handler = new TestSpanContextHandler(context);
    assertThat(handler.current()).isEqualTo(context);
  }

  @Test
  public void testAttach() {
    TestSpanContextHandler handler = new TestSpanContextHandler();
    handler.attach(context);
    assertThat(handler.current()).isEqualTo(context);
  }

  @Test
  public void testDetach() {
    TestSpanContextHandler handler = new TestSpanContextHandler(
        TestSpanContextHandler.TEST_CONTEXT);
    handler.attach(context).detach();
    assertThat(handler.current()).isEqualTo(TestSpanContextHandler.TEST_CONTEXT);
  }
}
