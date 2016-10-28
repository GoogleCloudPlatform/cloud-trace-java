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

public class ThreadLocalTraceContextHandler implements TraceContextHandler {
  private static final ThreadLocal<DefaultTraceContextHandler> handlers =
      new ThreadLocal<DefaultTraceContextHandler>();
  private final TraceContext root;

  public ThreadLocalTraceContextHandler(TraceContext root) {
    this.root = root;
  }

  @Override
  public TraceContext current() {
    return getHandler().current();
  }

  @Override
  public void push(TraceContext context) {
    getHandler().push(context);
  }

  @Override
  public TraceContext pop() {
    return getHandler().pop();
  }

  private DefaultTraceContextHandler getHandler() {
    DefaultTraceContextHandler handler = handlers.get();
    if (handler == null) {
      handler = new DefaultTraceContextHandler(root);
      handlers.set(handler);
    }
    return handler;
  }
}
