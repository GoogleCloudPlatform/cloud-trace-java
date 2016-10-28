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

package com.google.cloud.trace.core;

import static org.junit.Assert.assertSame;

import java.math.BigInteger;
import org.junit.Test;

public class DefaultTraceContextHandlerTest {

  @Test
  public void testPushPop() {
    SpanContext root = new SpanContext(new TraceId(BigInteger.valueOf(3)), new SpanId(4),
        TraceOptions.forTraceEnabled());
    SpanContext child1 = new SpanContext(new TraceId(BigInteger.valueOf(3)), new SpanId(5),
        TraceOptions.forTraceEnabled());
    SpanContext child2 = new SpanContext(new TraceId(BigInteger.valueOf(3)), new SpanId(6),
        TraceOptions.forTraceEnabled());

    TraceContextHandler handler = new DefaultTraceContextHandler(root);

    assertSame(root, handler.current());
    handler.push(child1);
    assertSame(child1, handler.current());
    handler.push(child2);
    assertSame(child2, handler.current());
    handler.pop();
    assertSame(child1, handler.current());
  }
}