package com.google.cloud.trace.core;

/**
 * Represents the current context of a Trace.
 */
public class TraceContext {
  private final SpanContext current, parent;

  public TraceContext(SpanContext current, SpanContext parent) {
    this.current = current;
    this.parent = parent;
  }

  /**
   * Returns the current {@link SpanContext}
   * @return the current {@link SpanContext}
   */
  public SpanContext getCurrent() {
    return current;
  }

  /**
   * Returns the parent of the current {@link SpanContext}
   * @return the parent of the current {@link SpanContext}
   */
  public SpanContext getParent() {
    return parent;
  }
}
