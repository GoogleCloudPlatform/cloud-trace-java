package com.google.cloud.trace;

import com.google.cloud.trace.core.SpanContext;
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
  public SpanContext attach(SpanContext context) {
    Context previous = Context.current().withValue(contextKey, context).attach();
    return contextKey.get(previous);
  }

  @Override
  public void detach(SpanContext toAttach) {
    Context current = Context.current();
    current.detach(current.withValue(contextKey, toAttach));
  }
}
