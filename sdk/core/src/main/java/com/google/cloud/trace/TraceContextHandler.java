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
 * An interface that represents a stack of {@link TraceContext}s.
 */
public interface TraceContextHandler {
  /**
   * Returns the current trace context.
   *
   * @return the current trace context.
   */
  TraceContext current();

  /**
   * Pushes a new trace context onto the top of the stack.
   *
   * @param context a trace context to push onto the stack.
   */
  void push(TraceContext context);

  /**
   * Pops the context off the top of the stack and returns it.
   *
   * @return the context on the top of the stack or null if the stack if empty.
   */
  TraceContext pop();

  /**
   * Makes the trace context on top of the stack the new root context.
   * The stack cannot be popped below this context until restore(TraceContextHandlerState) is called.
   * @return The {@link TraceContextHandlerState} for the stack prior to adding the top element.
   */
  TraceContextHandlerState replace();

  /**
   * Add a new root context to the top of the stack.
   * The stack cannot be popped below this context until restore(TraceContextHandlerState) is called.
   * @param newRoot The new root trace context to attach.
   * @return The {@link TraceContextHandlerState} for the stack prior to adding the new root.
   */
  TraceContextHandlerState replace(TraceContext newRoot);

  /**
   * Restore the stack to the provided state.
   * This will remove trace contexts that have been pushed (but not popped) since the call to replace()
   * that returned the provided {@link TraceContextHandlerState}
   * @param toRestore The {@link TraceContextHandlerState} returned by replace()
   */
  void restore(TraceContextHandlerState toRestore);

  /**
   * Represents the state of the stack in the TraceContextHandler
   */
  class TraceContextHandlerState {
    private final int rootIndex;
    private final int size;

    public TraceContextHandlerState(int rootIndex, int size) {
      this.rootIndex = rootIndex;
      this.size = size;
    }

    public int getRootIndex() {
      return rootIndex;
    }

    public int getSize() {
      return size;
    }
  }
}
