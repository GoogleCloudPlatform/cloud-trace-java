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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * An abstract span context handler that implements a stack of span contexts.
 */
public abstract class AbstractTraceContextHandler implements TraceContextHandler {
  private static final Logger logger = Logger.getLogger(
      AbstractTraceContextHandler.class.getName());

  private final ArrayDeque<SpanContext> contextStack;

  /**
   * Creates a new span context handler.
   *
   * @param rootContext a span context that serves as the root span context. This is the bottom of
   * the stack and cannot be removed.
   */
  public AbstractTraceContextHandler(SpanContext rootContext) {
    this.contextStack = new ArrayDeque<SpanContext>(Collections.singleton(rootContext));
  }

  @Override
  public SpanContext current() {
    return contextStack.peekFirst();
  }

  @Override
  public void push(SpanContext context) {
    contextStack.addFirst(context);
    doPush(context);
  }

  @Override
  public SpanContext pop() {
    if (contextStack.size() > 1) {
      SpanContext context = contextStack.removeFirst();
      doPop(current());
      return context;
    }
    logger.warning("Too many calls to AbstractTraceContextHandler.pop().");
    return null;
  }

  /**
   * Performs additional actions after a new span context has been pushed onto the stack.
   *
   * @param context the span context pushed onto the stack.
   */
  protected abstract void doPush(SpanContext context);

  /**
   * Performs additional actions after a span context has been popped off the stack.
   *
   * @param context the span context popped off the stack.
   */
  protected abstract void doPop(SpanContext context);
}
