// Copyright 2015 Google Inc. All rights reserved.
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

package com.google.cloud.trace.sdk.gae;

import com.google.cloud.trace.sdk.ThreadTraceContext;
import com.google.cloud.trace.sdk.TraceSpanData;
import com.google.cloud.trace.sdk.TraceSpanDataListener;

/**
 * An AppEngine specific listener makes sure the current context of AppEngine is in sync
 * with the top of the {@link ThreadTraceContext} stack.
 */
public final class AppEngineTraceSpanDataListener implements TraceSpanDataListener {
  private final TraceSpanDataListener listener;

  public AppEngineTraceSpanDataListener(TraceSpanDataListener listener) {
    this.listener = listener;
  }

  @Override
  public void onStart(TraceSpanData spanData) {
    listener.onStart(spanData);
    AppEngineCurrentTraceContext.getInstance().set(ThreadTraceContext.peek());
  }

  @Override
  public void onEnd(TraceSpanData spanData) {
    listener.onEnd(spanData);
    AppEngineCurrentTraceContext.getInstance().set(ThreadTraceContext.peek());
  }
}

