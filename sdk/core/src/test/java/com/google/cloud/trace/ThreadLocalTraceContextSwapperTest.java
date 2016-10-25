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

import static org.junit.Assert.assertSame;

import com.google.cloud.trace.util.SpanId;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceId;
import com.google.cloud.trace.util.TraceOptions;
import java.math.BigInteger;
import org.junit.Test;

public class ThreadLocalTraceContextSwapperTest {

  @Test
  public void testTwoThreads() {
    final TraceContext defaultRoot = new TraceContext(new TraceId(BigInteger.valueOf(2)), new SpanId(3), TraceOptions.forTraceEnabled());
    final TraceContext root1 = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(4), TraceOptions.forTraceEnabled());
    final TraceContext child1 = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(5), TraceOptions.forTraceEnabled());
    final TraceContext root2 = new TraceContext(new TraceId(BigInteger.valueOf(4)), new SpanId(5), TraceOptions.forTraceDisabled());
    final TraceContext child2 = new TraceContext(new TraceId(BigInteger.valueOf(4)), new SpanId(6), TraceOptions.forTraceEnabled());

    final ThreadLocalTraceContextSwapper swapper = new ThreadLocalTraceContextSwapper(defaultRoot);

    assertSame(defaultRoot, swapper.currentHandler().current());
    TraceContextHandler newHandler = new DefaultTraceContextHandler(root1);
    TraceContextHandler previousHandler = swapper.swap(newHandler);
    assertSame(newHandler, swapper.currentHandler());
    swapper.currentHandler().push(child1);

    ThreadRunnable runnable = new ThreadRunnable(swapper, root2, child2);
    Thread thread2 = new Thread(runnable);
    thread2.start();
    try {
      thread2.join();
    } catch (InterruptedException e) {
      // Do nothing.
    }

    // Check the current thread context is still there.
    TraceContextHandler currentHandler = swapper.currentHandler();
    assertSame(child1, currentHandler.current());
    assertSame(child1, currentHandler.pop());
    assertSame(root1, currentHandler.current());

    // Check the results from the other thread.
    assertSame(defaultRoot, runnable.initialContext);
    assertSame(root2, runnable.contextAfterAttach);
    assertSame(child2, runnable.contextAfterPush);
    assertSame(root2, runnable.contextAfterPop);
    assertSame(defaultRoot, runnable.contextAfterDetach);

    // Detach the context handler from the current thread.
    swapper.swap(previousHandler);
    assertSame(previousHandler, swapper.currentHandler());

    // Swap in a null context handler and ensure push() still works.
    swapper.swap(null);
    swapper.currentHandler().push(child1);
    assertSame(child1, swapper.currentHandler().current());

    // Swap in a null context handler and ensure it returns the default root.
    swapper.swap(null);
    assertSame(defaultRoot, swapper.currentHandler().current());
  }

  private static class ThreadRunnable implements Runnable {

    private final ThreadLocalTraceContextSwapper swapper;
    private final TraceContext rootToAttach;
    private final TraceContext toPush;
    TraceContext initialContext;
    TraceContext contextAfterAttach;
    TraceContext contextAfterPush;
    TraceContext contextAfterPop;
    TraceContext contextAfterDetach;

    ThreadRunnable(ThreadLocalTraceContextSwapper swapper, TraceContext rootToAttach, TraceContext toPush) {
      this.swapper = swapper;
      this.rootToAttach = rootToAttach;
      this.toPush = toPush;
    }

    @Override
    public void run() {
      initialContext = swapper.currentHandler().current();
      TraceContextHandler previous = swapper.swap(new DefaultTraceContextHandler(rootToAttach));
      contextAfterAttach = swapper.currentHandler().current();
      swapper.currentHandler().push(toPush);
      contextAfterPush = swapper.currentHandler().current();
      swapper.currentHandler().pop();
      contextAfterPop = swapper.currentHandler().current();
      swapper.swap(previous);
      contextAfterDetach = swapper.currentHandler().current();
    }
  }

}