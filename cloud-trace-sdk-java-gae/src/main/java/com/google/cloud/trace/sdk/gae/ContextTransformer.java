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

import com.google.cloud.trace.sdk.TraceContext;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * Transforms between two forms of trace context:
 *     com.google.apphosting.api.CloudTraceContext
 *     com.google.cloud.trace.sdk.TraceContext
 */
public final class ContextTransformer {
  public static CloudTraceContext transform(TraceContext context) {
    return new CloudTraceContext(context.getTraceId().getBytes(StandardCharsets.UTF_8),
        context.getSpanId().longValue(), context.getOptions());
  }

  public static TraceContext transform(CloudTraceContext context) {
    return new TraceContext(new String(context.getTraceId(), StandardCharsets.UTF_8),
        BigInteger.valueOf(context.getSpanId()), context.getTraceMask());
  }
}

