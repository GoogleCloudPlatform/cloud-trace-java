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

package com.google.cloud.trace.service;

import com.google.appengine.api.labs.trace.Span;
import com.google.apphosting.api.CloudTraceContext;
import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.TraceContext;

/**
 * Implementation of {@link TraceContext} based on Google App Engine's Trace
 * Service API.
 */
class AppEngineTraceContext extends TraceContext {
  private final Span span;

  AppEngineTraceContext(Span span) {
    // TODO: TraceContext should be an interface.
    super(null, null);
    this.span = span;
  }

  Span getSpan() {
    return span;
  }

  @Override
  public SpanContext getCurrent() {
    return getSpanContext(span.getContext());
  }

  @Override
  public SpanContext getParent() {
    return getSpanContext(span.getParentContext());
  }

  private static SpanContext getSpanContext(CloudTraceContext cloudTraceContext) {
    // TODO: Implement this.
    return null;
  }
}
