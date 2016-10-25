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

import com.google.cloud.trace.util.TraceContext;

/**
 * An implementation of TraceContextSwapper that uses ThreadLocal storage to store TraceContextHandlers.
 *
 * A server-side RPC interceptor may use this to swap the TraceContextHandler so that a request
 * uses the {@link com.google.cloud.trace.util.TraceContext} provided in an HTTP header.
 * An {@link java.util.concurrent.Executor} may use this to swap the {@link TraceContextHandler} so
 * that children threads inherit the context from their parent thread.
 */
public class ThreadLocalTraceContextSwapper implements TraceContextSwapper {
  private static final ThreadLocal<TraceContextHandler> handlers = new ThreadLocal<TraceContextHandler>();
  private final TraceContext defaultRoot;

  /**
   * Creates a new TraceContextSwapper
   * @param defaultRoot The default root TraceContext that should be used for each Thread.
   */
  public ThreadLocalTraceContextSwapper(TraceContext defaultRoot) {
    this.defaultRoot = defaultRoot;
  }

  @Override
  public TraceContextHandler swap(TraceContextHandler newHandler) {
    TraceContextHandler previousHandler = currentHandler();
    handlers.set(newHandler);
    return previousHandler;
  }

  /**
   * Returns the current trace context handler for the current thread.
   *
   * @return the current trace context handler.
   */
  @Override
  public TraceContextHandler currentHandler() {
    TraceContextHandler handler = handlers.get();
    if (handler == null) {
      handler = new DefaultTraceContextHandler(defaultRoot);
      handlers.set(handler);
    }
    return handler;
  }
}