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

import com.google.cloud.trace.TraceContextHandler.TraceContextHandlerState;
import com.google.cloud.trace.util.SpanId;
import com.google.cloud.trace.util.TraceContext;
import com.google.cloud.trace.util.TraceId;
import com.google.cloud.trace.util.TraceOptions;
import java.math.BigInteger;
import org.junit.Test;

public class DefaultTraceContextHandlerTest {

  @Test
  public void testPushPop() {
    TraceContext root = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(4),
        TraceOptions.forTraceEnabled());
    TraceContext child1 = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(5),
        TraceOptions.forTraceEnabled());
    TraceContext child2 = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(6),
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

  @Test
  public void testReplaceRestore() {
    TraceContext defaultRoot =
        new TraceContext(new TraceId(BigInteger.valueOf(0)), new SpanId(0), new TraceOptions());
    TraceContext root1 = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(4),
        TraceOptions.forTraceEnabled());
    TraceContext child1 = new TraceContext(new TraceId(BigInteger.valueOf(3)), new SpanId(5),
        TraceOptions.forTraceEnabled());
    TraceContext root2 = new TraceContext(new TraceId(BigInteger.valueOf(4)), new SpanId(4),
        TraceOptions.forTraceEnabled());
    TraceContext child2 = new TraceContext(new TraceId(BigInteger.valueOf(4)), new SpanId(6),
        TraceOptions.forTraceEnabled());

    TraceContextHandler handler = new DefaultTraceContextHandler(defaultRoot);
    // [#defaultRoot#]
    assertSame(defaultRoot, handler.current());

    TraceContextHandlerState handlerRoot1 = handler.replace(root1);
    // [#defaultRoot#, #root1#]
    assertSame(root1, handler.current());

    handler.pop();
    // [#defaultRoot#, #root1#]
    // Shouldn't be able to pop below root.
    assertSame(root1, handler.current());

    handler.push(child1);
    // [#defaultRoot#, #root1#, child1]
    assertSame(child1, handler.current());

    TraceContextHandlerState handlerRoot2 = handler.replace(root2);
    // [#defaultRoot#, #root1#, child1, #root2#]
    assertSame(root2, handler.current());

    handler.pop();
    // [#defaultRoot#, #root1#, child1, #root2#]
    // Shouldn't be able to pop below root.
    assertSame(root2, handler.current());

    handler.push(child2);
    // [#defaultRoot#, #root1#, child1, #root2#, child2]
    assertSame(child2, handler.current());

    handler.restore(handlerRoot2);
    // [#defaultRoot#, #root1#, child1]
    assertSame(child1, handler.current());

    handler.pop();
    // [#defaultRoot#, #root1#]
    assertSame(root1, handler.current());

    handler.pop();
    // [#defaultRoot#, #root1#]
    //Shouldn't be able to pop below root
    assertSame(root1, handler.current());

    handler.restore(handlerRoot1);
    // [#defaultRoot#]
    assertSame(defaultRoot, handler.current());
  }
}