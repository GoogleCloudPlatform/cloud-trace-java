package com.google.cloud.trace;

import com.google.cloud.trace.util.TraceContext;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.logging.Logger;

public abstract class AbstractTraceContextHandler implements TraceContextHandler {
  private static final Logger logger = Logger.getLogger(
      AbstractTraceContextHandler.class.getName());

  private final ArrayDeque<TraceContext> contextStack;

  public AbstractTraceContextHandler(TraceContext rootContext) {
    this.contextStack = new ArrayDeque<TraceContext>(Collections.singleton(rootContext));
  }

  @Override
  public TraceContext current() {
    return contextStack.peekFirst();
  }

  @Override
  public void push(TraceContext context) {
    contextStack.addFirst(context);
    doPush(context);
  }

  @Override
  public TraceContext pop() {
    if (contextStack.size() > 1) {
      TraceContext context = contextStack.removeFirst();
      doPop(current());
      return context;
    }
    logger.warning("Too many calls to AbstractTraceContextHandler.pop().");
    return null;
  }

  protected abstract void doPush(TraceContext context);
  protected abstract void doPop(TraceContext context);
}
