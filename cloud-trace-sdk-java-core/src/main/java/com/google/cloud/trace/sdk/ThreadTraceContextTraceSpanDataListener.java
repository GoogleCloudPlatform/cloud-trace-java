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

/**
 * Trace span data listener that maintains the {@link ThreadTraceContext}'s stack
 * of trace contexts on the current thread.
 */
public class ThreadTraceContextTraceSpanDataListener implements TraceSpanDataListener {
  @Override
  public void onStart(TraceSpanData spanData) {
    ThreadTraceContext.push(spanData.getContext());
  }

  @Override
  public void onEnd(TraceSpanData spanData) {
    TraceContext context = ThreadTraceContext.pop();
    if (!context.equals(spanData.getContext())) {
      throw new IllegalStateException("ThreadTraceContext is out-of-sync");
    }
  }
}
