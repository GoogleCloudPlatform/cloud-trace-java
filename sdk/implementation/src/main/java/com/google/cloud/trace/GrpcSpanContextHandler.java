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
import com.google.cloud.trace.core.SpanContextHandle;
import io.grpc.Context;
import io.grpc.Context.Key;

/**
 * A {@link SpanContextHandler} that stores the {@link SpanContext} in the gRPC {@link Context}.
 */
public class GrpcSpanContextHandler implements SpanContextHandler {
  private final Key<SpanContext> contextKey;

  public GrpcSpanContextHandler(Key<SpanContext> contextKey) {
    this.contextKey = contextKey;
  }

  public GrpcSpanContextHandler(SpanContext defaultValue) {
    this(Context.keyWithDefault("spanContext", defaultValue));
  }

  @Override
  public SpanContext current() {
    return contextKey.get();
  }

  @Override
  public SpanContextHandle attach(SpanContext context) {
    Context current = Context.current().withValue(contextKey, context);
    Context previous = current.attach();
    return new GrpcSpanContextHandle(current, previous);
  }


  private class GrpcSpanContextHandle implements SpanContextHandle {
    private final Context current, previous;

    private GrpcSpanContextHandle(Context current, Context previous) {
      this.current = current;
      this.previous = previous;
    }

    @Override
    public SpanContext getCurrentSpanContext() {
      return contextKey.get(current);
    }

    @Override
    public void detach() {
      current.detach(previous);
    }
  }
}
