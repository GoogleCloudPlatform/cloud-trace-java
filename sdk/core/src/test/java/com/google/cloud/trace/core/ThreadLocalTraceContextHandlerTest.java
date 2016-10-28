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

import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.ThreadLocalTraceContextHandler;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.core.TraceContextHandler;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import java.math.BigInteger;
import org.junit.Test;

public class ThreadLocalTraceContextHandlerTest {

  @Test
  public void testTwoThreads() {
    final TraceContext defaultRoot = new TraceContext(new TraceId(BigInteger.valueOf(2)), new SpanId(3), TraceOptions
        .forTraceEnabled());
    final TraceContext root1 = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(4), TraceOptions.forTraceEnabled());
    final TraceContext child1 = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(5), TraceOptions.forTraceEnabled());
    final TraceContext root2 = new TraceContext(new TraceId(BigInteger.valueOf(4)), new SpanId(5), TraceOptions.forTraceDisabled());
    final TraceContext child2 = new TraceContext(new TraceId(BigInteger.valueOf(4)), new SpanId(6), TraceOptions.forTraceEnabled());

    final TraceContextHandler handler = new ThreadLocalTraceContextHandler(defaultRoot);

    assertSame(defaultRoot, handler.current());
    handler.push(root1);
    assertSame(root1, handler.current());
    handler.push(child1);
    assertSame(child1, handler.current());

    ThreadRunnable runnable = new ThreadRunnable(handler, root2, child2);
    Thread thread2 = new Thread(runnable);
    thread2.start();
    try {
      thread2.join();
    } catch (InterruptedException e) {
      // Do nothing.
    }

    // Check the current thread context is still there.
    assertSame(child1, handler.current());
    assertSame(child1, handler.pop());
    assertSame(root1, handler.current());

    // Check the results from the other thread.
    assertSame(defaultRoot, runnable.initialContext);
    assertSame(root2, runnable.contextAfterPush1);
    assertSame(child2, runnable.contextAfterPush2);
    assertSame(root2, runnable.contextAfterPop1);
    assertSame(defaultRoot, runnable.contextAfterPop2);
  }

  private static class ThreadRunnable implements Runnable {

    private final TraceContextHandler handler;
    private final TraceContext push1, push2;
    TraceContext initialContext;
    TraceContext contextAfterPush1;
    TraceContext contextAfterPush2;
    TraceContext contextAfterPop1;
    TraceContext contextAfterPop2;

    ThreadRunnable(TraceContextHandler handler, TraceContext push1, TraceContext push2) {
      this.handler = handler;
      this.push1 = push1;
      this.push2 = push2;
    }

    @Override
    public void run() {
      initialContext = handler.current();
      handler.push(push1);
      contextAfterPush1 = handler.current();
      handler.push(push2);
      contextAfterPush2 = handler.current();
      handler.pop();
      contextAfterPop1 = handler.current();
      handler.pop();
      contextAfterPop2 = handler.current();
      // Explicitly pushing something else onto the stack and leaving it
      // to test that the other thread is not affected.
      handler.push(push1);
    }
  }
}
