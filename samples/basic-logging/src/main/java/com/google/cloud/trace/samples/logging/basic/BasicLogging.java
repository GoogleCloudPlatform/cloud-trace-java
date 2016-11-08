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

package com.google.cloud.trace.samples.logging.basic;

import com.google.cloud.trace.Trace;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.ThrowableStackTraceHelper;
import com.google.cloud.trace.core.TraceContext;

public class BasicLogging {
  public static void main(String[] args) {
    Tracer tracer = Trace.getTracer();

    // Create a span using the given timestamps.
    TraceContext context = tracer.startSpan("my span 1");
    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    tracer.setStackTrace(context, stackTraceBuilder.build());
    tracer.endSpan(context);
  }
}
