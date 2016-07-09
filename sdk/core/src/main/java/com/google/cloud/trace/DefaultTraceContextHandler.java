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
 * A concrete implementation of a trace context handler.
 */
public class DefaultTraceContextHandler extends AbstractTraceContextHandler {
  /**
   * Creates a new trace context handler.
   *
   * @param context a trace context that serves as the root trace context.
   */
  public DefaultTraceContextHandler(TraceContext context) {
    super(context);
  }

  /**
   * Does nothing when a new trace context is pushed onto the stack.
   */
  @Override
  public void doPush(TraceContext context) {
    // Do nothing else.
  }

  /**
   * Does nothing when a trace context is popped off the stack.
   */
  @Override
  public void doPop(TraceContext context) {
    // Do nothing else.
  }
}
