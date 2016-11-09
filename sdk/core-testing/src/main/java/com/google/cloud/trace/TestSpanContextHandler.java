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

import com.google.cloud.trace.core.SpanContext;
import com.google.cloud.trace.core.SpanId;
import com.google.cloud.trace.core.TraceId;
import com.google.cloud.trace.core.TraceOptions;
import java.math.BigInteger;

public class TestSpanContextHandler implements SpanContextHandler {
  public static final SpanContext TEST_CONTEXT = new SpanContext(
      new TraceId(BigInteger.TEN),
      new SpanId(500),
      TraceOptions.forTraceEnabled());

  private SpanContext currentContext;

  public TestSpanContextHandler() {
    this(TEST_CONTEXT);
  }

  public TestSpanContextHandler(SpanContext context) {
    this.currentContext = context;
  }

  @Override
  public SpanContext current() {
    return currentContext;
  }

  @Override
  public SpanContext attach(SpanContext context) {
    SpanContext temp = currentContext;
    this.currentContext = context;
    return temp;
  }

  @Override
  public void detach(SpanContext toAttach) {
    this.currentContext = toAttach;
  }
}
