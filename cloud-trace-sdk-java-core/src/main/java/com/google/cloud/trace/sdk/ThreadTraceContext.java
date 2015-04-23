// Copyright 2015 Google Inc. All rights reserved.
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

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Holds a stack of {@link TraceContext}s. The user is (largely) responsible
 * for maintaining this stack insofar as they must open and close new TraceContexts
 * in the expected order. In practice, this means maintaining a correct hierarchy
 * of {@link TraceSpanData}s, as TraceSpanData pushes and pops automatically
 * as they are opened and closed.
 * 
 * If a parent is closed before its child, the wrong context may get propagated to
 * subsequent callees.
 * 
 * If a TraceSpanData is closed multiple times, you will see a NoSuchElementException
 * from the pop in this class.
 */
public class ThreadTraceContext {
  private static final ThreadLocal<LinkedBlockingDeque<TraceContext>> contextsHolder =
      new ThreadLocal<LinkedBlockingDeque<TraceContext>>() {
    @Override
    protected java.util.concurrent.LinkedBlockingDeque<TraceContext> initialValue() {
      return new LinkedBlockingDeque<>();
    }
  };

  public static void push(TraceContext context) {
    contextsHolder.get().push(context);
  }
  
  public static TraceContext pop() {
    return contextsHolder.get().pop();
  }
  
  public static TraceContext peek() {
    return contextsHolder.get().peek();
  }
  
  public static boolean isEmpty() {
    return contextsHolder.get().isEmpty();
  }

  public static void clear() {
    contextsHolder.get().clear();
  }
}
